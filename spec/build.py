#!/usr/bin/env python3.11
# -*- coding: utf-8 -*-
"""机械合并：把公共库 + 各组形态图 + 契约文档 + 渲染层拼成自包含 index.html。
   文档真源在 contracts/*.md，这里只做 markdown→HTML 注入，不复制第二份内容。"""
import pathlib, subprocess, tempfile, json, re

B = pathlib.Path('/workspace/shengye/spec')
C = pathlib.Path('/workspace/shengye/contracts')
OUT = B / 'index.html'

def read(p, required=True):
    f = pathlib.Path(p)
    if not f.exists():
        if required: raise SystemExit(f'缺少文件：{f}')
        return ''
    return f.read_text(encoding='utf-8')

def md2html(text):
    import markdown
    return markdown.markdown(text, extensions=['tables', 'fenced_code'])

shell = read(B / 'shell.html')
ui = read(B / 'ui.js')
screens = '\n'.join(read(p) for p in sorted(B.glob('screens_*.js')))
app = read(B / 'app.js')

contract = (read(C / 'STATE_VOCAB.md') + '\n\n---\n\n' + read(C / 'GLASS_TOKENS.md'))
check = read(C / 'ACCEPTANCE.md')
docs = 'const DOCS = ' + json.dumps({
    'contract': md2html(contract),
    'check': md2html(check),
}, ensure_ascii=False) + ';\n'

parts = {
    '/*@@UI@@*/':      ui,
    '/*@@SCREENS@@*/': screens,
    '/*@@DOCS@@*/':    docs,
    '/*@@APP@@*/':     app,
}
html = shell
for slot, content in parts.items():
    if slot not in html: raise SystemExit(f'外壳缺少占位符 {slot}')
    html = html.replace(slot, content)

OUT.write_text(html, encoding='utf-8')

# 全量 JS 语法自检
js = ui + '\n' + screens + '\n' + docs + '\n' + app
with tempfile.NamedTemporaryFile('w', suffix='.js', delete=False, encoding='utf-8') as f:
    f.write(js); tmp = f.name
r = subprocess.run(['node', '--check', tmp], capture_output=True, text=True)
if r.returncode != 0:
    raise SystemExit('JS 语法自检失败：\n' + r.stderr)

ids = re.findall(r"id:'([A-H][0-9]+)'", screens)
dups = sorted({x for x in ids if ids.count(x) > 1})
print(f'构建完成 -> {OUT}  ({OUT.stat().st_size} bytes)')
print(f'形态图 {len(ids)} 张 | 组 {sorted(set(i[0] for i in ids))} | 重复 id: {dups or "无"}')
print('JS 全量语法自检：OK')
