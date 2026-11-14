# 踩过的坑（别再踩第二遍）

> 派活时连同 `roles/*.md` 一起附给子智能体。这里每条都是**真实踩过、真实修过**的，
> 不是假设性风险。新增条目前必须写清「现象 → 根因 → 正确做法」。

## 1. Kotlin 注释里出现 `/*` 会吞掉整个文件

**现象**：编译报 `AppState.kt:57:1 Syntax error: Unclosed comment`，同文件里所有类型（`TaskState` / `SubPhase` / `PlayMode`）在别的文件里报 `Unresolved reference`。

**根因**：Kotlin 的块注释**可嵌套**。注释正文里写了 `同步 roles/*.md`，那个 `/*` 开始了一层新注释，后面第一个 `*/` 只关闭内层，外层一路吞到文件末尾。

**正确做法**：注释里不要出现 `/*`。要提文件通配就写「roles 下各档案」，或用 `roles/ .md`。

**自查**（一行，跑完无输出就是干净）：

```bash
cd android && python3 -c "
import pathlib
for p in pathlib.Path('.').rglob('*.kt'):
    s=p.read_text()
    if s.count('/*')!=s.count('*/'): print(p, s.count('/*'), s.count('*/'))
"
```

## 2. 包名 / namespace 不能用 Java 关键字

**现象**：`:platform:native` 构建直接失败——`Namespace 'com.shengye.platform.native' is not a valid Java package name as 'native' is a Java keyword`。

**正确做法**：模块名按功能起，别用 `native` / `new` / `class` / `interface` / `default` / `package` / `static` / `final` 这类词。本项目已改为 `:platform:tts`，namespace `com.shengye.platform.tts`。

## 3. Int 字面量不会自动变成 Long

**现象**：`Progress.etaMs(0, 10, 5000)` 编译失败，因为第三个参数是 `Long`。

**正确做法**：`5000L`。Kotlin 不做隐式数值拓宽。

## 4. 本机 `git push` 不通，必须走 API

**现象**：`git push` 卡死 2 分钟以上，报 `Failed to connect to github.com port 443` 或无声挂起；但同一时刻 `curl https://github.com` 是 200，`gh` 也能用。

**根因**：沙盒到 GitHub 的 git 上传通道（`git-receive-pack` 的大流量）被中断，TCP/TLS 握手能过，数据发不出去。

**正确做法**：用 `tools/push_api.py`（Git Data API，逐 blob 上传 + 建树 + 建提交 + 移动 ref）。它是幂等的，内容没变会跳过。

## 5. CI 里 `android-actions/setup-android@v3` 会挂

**现象**：`Run sdkmanager tools` 报 `Failed to find package 'tools'`，整个 job 在第 4 步失败。

**正确做法**：ubuntu-latest 已预装 SDK，缺 `cmdline-tools` 就自己下官方 zip 补上。Gradle 同理，直接下发行包，少一层第三方 action 就少一个变量。见 `.github/workflows/android.yml`。

## 6. 子智能体「零落盘」多半是额度，不是任务太大

**现象**：任务跑了十几分钟，返回 `Empty stream`，或干脆静默消失（无通知、无文件、`TaskOutput` 报找不到任务），一个字节都没写。

**根因**：免费额度不透明，烧到某处静默断流。

**正确做法**：任务说明第一条强制「第一个动作必须是 Write，先落一个可编译的骨架，再往里填」；单批 ≤10 张 / 1 个文件；失败先怀疑额度，再怀疑规模。

## 7. 界面规格里的组件默认值陷阱

详见 `roles/ui.md`「实测踩坑」。高频的几条：

- `U.R(inner, cls)` 只收一个 inner
- `U.segGrid` 颜色键是 `done / run / redo / wait`
- `U.stateChip` 没有 `stopped`
- `U.bar` 已是页面大标题，别再叠 `U.hero`
- 除播放态外一律 `mini:false`——旧默认值让 18/18 张图都挂着「未在播放」占位条

## 8. 状态词表一处定义，别自己拼

**现象**：旧版同一个意思有 6 套叫法；新代码里一度把「导入中」写成「制作中」。

**正确做法**：所有对外可见的状态词只从 `contracts/STATE_VOCAB.md` 与 `contract/src/.../AppState.kt` 的枚举里取。导入不是制作，用导入态。改一个词要同步契约与 `roles/*.md`。

## 9. XML 注释里不能出现 `--`

**现象**：资源合并失败——`The string "--" is not permitted within comments`。注释里写了 `GLASS_TOKENS.md 的 --wall1 / --accent`，那两个连字符直接让 build 挂掉。

**正确做法**：XML 注释里不要写双连字符，去掉前缀只写 `wall1 / accent`。

**自查**：

```bash
cd android && python3 -c "
import pathlib,re
for p in pathlib.Path('.').rglob('*.xml'):
    s=p.read_text(encoding='utf-8',errors='replace')
    for m in re.finditer(r'<!--(.*?)-->', s, re.S):
        if '--' in m.group(1): print(p, m.group(1)[:60])
"
```

## 10. 中文正则里别加 `\b`

**现象**：分章正则写了 `第X章\b`，结果一章都识别不出来。

**根因**：Java 正则的 `\b` 基于 `\w`（字母数字下划线），中文字符不算 `\w`，所以「章」和后面的空格之间**没有**词边界，`\b` 永远不成立。

**正确做法**：中文匹配不要用 `\b`，靠 `^` 行首 + 明确的字符类来限定。
