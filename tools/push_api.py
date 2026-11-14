#!/usr/bin/env python3
"""把当前工作树推送成远端 main 上的一个新提交（走 GitHub REST API）。

为什么不用 git push：
    本机沙盒到大仓的 git 上传通道会被中断（TCP/TLS 能连上，数据发不出去），
    但 api.github.com 完全可用。所以用 Git Data API 逐 blob 上传再建树建提交。

用法：
    GH_TOKEN=xxx python3 tools/push_api.py                 # 用 HEAD 的 commit message
    GH_TOKEN=xxx python3 tools/push_api.py -m "消息"        # 指定 message
    GH_TOKEN=xxx python3 tools/push_api.py --dry-run       # 只看会推什么

行为：
    - 文件清单 = git ls-files（尊重 .gitignore，未跟踪文件不会被推）
    - 远端 parent = 远端 main 当前 sha，历史线性前进，不 force
    - 幂等：内容没变时远端 tree sha 不变，会跳过建提交
"""
import argparse, base64, json, os, subprocess, sys, urllib.error, urllib.request
from concurrent.futures import ThreadPoolExecutor

OWNER, REPO = "zjh1934", "shengye"
BASE = f"https://api.github.com/repos/{OWNER}/{REPO}"
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def api(method, url, payload=None):
    data = json.dumps(payload).encode() if payload is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Authorization", f"Bearer {os.environ['GH_TOKEN']}")
    req.add_header("Accept", "application/vnd.github+json")
    req.add_header("X-GitHub-Api-Version", "2022-11-28")
    if data:
        req.add_header("Content-Type", "application/json")
    for attempt in range(4):
        try:
            with urllib.request.urlopen(req, timeout=90) as r:
                return json.loads(r.read().decode() or "{}")
        except urllib.error.HTTPError as e:
            body = e.read().decode()[:400]
            if e.code in (502, 503, 504) and attempt < 3:
                continue
            raise SystemExit(f"[FAIL] {method} {url} -> {e.code}\n{body}")
        except (urllib.error.URLError, TimeoutError) as e:
            if attempt < 3:
                continue
            raise SystemExit(f"[FAIL] 网络不可达: {e}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("-m", "--message")
    ap.add_argument("--dry-run", action="store_true")
    a = ap.parse_args()

    if not os.environ.get("GH_TOKEN"):
        raise SystemExit("需要 GH_TOKEN 环境变量")

    files = subprocess.run(["git", "ls-files"], cwd=ROOT,
                           capture_output=True, text=True, check=True).stdout.split()
    message = a.message or subprocess.run(
        ["git", "log", "-1", "--pretty=%B"], cwd=ROOT,
        capture_output=True, text=True).stdout.strip()
    if not message:
        raise SystemExit("没有 commit message")

    print(f"文件 {len(files)} 个 -> {OWNER}/{REPO}")
    if a.dry_run:
        for f in files:
            print("  ", f)
        return

    def blob(path):
        with open(os.path.join(ROOT, path), "rb") as fh:
            raw = fh.read()
        r = api("POST", f"{BASE}/git/blobs",
                {"content": base64.b64encode(raw).decode(), "encoding": "base64"})
        return {"path": path, "mode": "100644", "type": "blob", "sha": r["sha"]}

    with ThreadPoolExecutor(max_workers=8) as ex:
        entries = list(ex.map(blob, files))

    tree = api("POST", f"{BASE}/git/trees", {"tree": entries})["sha"]
    head = api("GET", f"{BASE}/git/ref/heads/main")["object"]["sha"]
    head_tree = api("GET", f"{BASE}/git/commits/{head}")["tree"]["sha"]

    if tree == head_tree:
        print(f"内容无变化，跳过。远端 main = {head[:8]}")
        return

    commit = api("POST", f"{BASE}/git/commits",
                 {"message": message, "tree": tree, "parents": [head]})["sha"]
    api("PATCH", f"{BASE}/git/refs/heads/main", {"sha": commit, "force": False})
    print(f"[OK] main {head[:8]} -> {commit[:8]}")
    print(f"     https://github.com/{OWNER}/{REPO}/commit/{commit}")


if __name__ == "__main__":
    main()
