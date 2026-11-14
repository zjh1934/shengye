# 形态图绘制规格（子智能体必读 · 唯一契约）

> 你没有长期记忆，本文件 + 你的 `roles/*.md` 就是你的记忆。
> **你画的是「重做之后」的界面规格，不是旧版复刻。** 旧版只作为功能比对（`CONSTITUTION.md`），不照抄它的布局与文案。

## 1. 你的产物

- 只写一个文件：任务说明里指定的 `spec/screens_X.js`。
- 文件内容**只能是**一串 `S({...});` 调用，不要写 `const` 声明 `U / P / S / GROUPS / SHOTS / DOCS`（这些由总工程师定义），不要写 `<script>` 标签，不要写注释以外的作用域代码。
- **禁止引入任何图片 / 外部资源 / 字体 / CDN**。纯 HTML + CSS class + inline SVG。

## 2. 唯一的图册 API

```js
S({
  id:'A1',                  // 组字母 + 序号，见任务说明分配
  g:'A',                    // 所属组
  name:'首页 · 空态',        // 名称，简短
  state:'无书',              // 这一屏表达的「运行态」，用 STATE_VOCAB.md 里的词
  trigger:'首次安装',        // 触发条件，一句
  spec:'CONSTITUTION 内容与导入 / ARCHITECTURE feature:library',  // 依据
  render: ()=>P({ ... })    // 返回一屏 HTML
});
```

`P(o)` = `U.phone(o)` 的快捷包装，默认已带状态栏、迷你播放条、底部四空间 tab。可用参数：

| 参数 | 说明 |
|---|---|
| `appbar` | `U.bar({back:true/false, title:'…', sub:'…', trailing:['文字按钮']})` |
| `body` | 内容区 HTML（用 `<div class="scroll">…</div>` 包一层可滚动） |
| `tab` | 0=我的书 1=这一本 2=正在做 3=正在听 |
| `mini` | 传 `false` 隐藏迷你条；或 `U.mini({title,sub,playing:true})` |
| `tabs` | 传 `false` 隐藏 tab（**仅全屏浮层**如播放器全屏 / 调音台展开时用） |

## 3. 可用组件（只能用这些，不得自造）

| 组件 | 用途 |
|---|---|
| `U.bar(o)` / `U.tabs(on)` / `U.mini(o)` / `U.sb()` / `U.phone(o)` | 骨架 |
| `U.C(inner,cls)` / `U.glass(inner,cls)` | 普通卡 / 玻璃卡 |
| `U.R(inner,cls)` / `U.T(t,cls)` / `U.B(t,cls)` / `U.chip(t,cls)` | 行 / 文字 / 按钮 / 胶囊 |
| `U.t.b` 等写法是错的，正确是 `U.T('文字','b')`；cls 可用 `b`(粗) `s`(次) `p`(主色) `big` `g` `r` | 文字样式 |
| 按钮 cls：`pri`(主) `line`(描边) `ghost`(文字) `sm`(小) | 按钮样式 |
| 胶囊 cls：`g`(好) `w`(警) `r`(错) `line`(中性) | 胶囊样式 |
| `U.bar2(pct)` | 进度条（**全 App 一根，一个百分比**） |
| `U.kv(k,v)` / `U.divider()` / `U.spin()` / `U.skel(n)` | 键值 / 分隔 / 旋转 / 骨架 |
| `U.dlg({title,text,body,buttons:[...]})` | 弹窗（含遮罩） |
| `U.menu({items:[...],style:'top:…;left:…'})` / `U.toast(t)` | 菜单 / Toast |
| `U.ball({v,sub,top})` / `U.notif({title,text,action,time})` | 悬浮球 / 通知 |
| `U.field(t,ph,area)` / `U.sw(on)` / `U.slider(label,val,unit)` | 输入框 / 开关 / 滑块 |
| `U.ring(v,sub)` / `U.hero(title,sub)` | 状态环 / 大标题 |
| `U.stat({title,pct,line,actions:[...]})` | **状态卡**（唯一进度口径，任务页/目录页共用） |
| `U.track({title,sub,icon,right,cls})` | 书 / 章 / 曲目行 |
| `U.stateChip(kind)` | 章状态胶囊；kind ∈ `done/running/queued/idle/failed/paused` |
| `U.segGrid([{c:'done',t:'1'},…])` / `U.legend()` | 块网格（**只在任务页详情**）/ 图例 |
| `U.voices(names,on)` / `U.modes(labels,on)` | 音色三分类 / 模式切换 |
| `U.cover({txt,small})` / `U.playctl({playing})` / `U.empty(t,btns)` | 封面 / 播放键 / 空态 |
| `U.ic(name)` / `U.svg(path,size)` | 图标（名字见 `ui.js` 的 `U.I`） |
| `U.dim(inner)` | 自定义遮罩层 |

**颜色 / 模糊 / 圆角 / 字号一律由 CSS class 提供，禁止写 `style="color:#…"` 之类的内联色值。**
例外：进度条宽度 `width:${pct}%`、滑块值、悬浮球角度这类**数据驱动**的 inline style 允许。

## 4. 内容硬约束（违反即打回）

1. **不啰嗦**：界面里只允许出现「状态 · 书名 · 章名 · 操作名 · 数字」。
   **禁止**：`怎么念 / 怎么跑 / 怎么拼，都在设置页`、`改一次对所有书生效`、`对所有书生效`、`只删这一本：音频、章节表、收听与阅读位置会一起删掉…`、任何解释原理/后果/用法的句子。
   弹窗正文最多一句；危险确认只留「删什么 + 不可撤销」。
2. **运行状态必须有**：每一屏都要能读出「现在在做什么 / 做到哪 / 百分比 / 还剩多少 / 能做什么」。
3. **状态词只能用 `STATE_VOCAB.md`**：`排队中 / 准备中 / 制作中 / 已暂停 / 已停止 / 已达时长上限 / 失败 / 已完成 / 未开始 / 可听 / 待生成 / 合成中 / 已生成 / 需重做 / 正在拼接 / 正在转码`。
   **禁止**旧版词：`正在往下做`、`需重新确认`、`没完成`、`整本制作`、`已出章音频`、`段音频`、`按顺序播放`、`去任务页看`、`Producer:` 前缀。
4. **一根进度条**：同一屏里最多一根 `U.bar2` / `U.stat`，一个百分比口径。
5. **职责分开**：
   - 我的书（A）= **首页顶部约 1/3 屏固定为「粘贴文本 → 合成」区**，其余给书单；书单里最多给单本一根进度；禁止「整本制作 / 已出章音频」双口径四件套
   - 这一本（B）= 只放章节列表 + 一个总览状态卡，**不显示块网格 / 双进度 / 队列跳转 / 多选工具条**
   - 正在做（C）= 全 App 唯一状态中心，**不列音频成品**（成品单独一个入口）
   - 正在听（E）= 播放器，不管制作
6. **底部四空间 tab 常驻**（除全屏浮层）；高频操作按钮**必须带文字**。
7. **深浅色都成立**：只用 CSS 变量 / class，不写死颜色。

## 5. 自检（交回前必须自己跑）

```bash
cd /workspace/shengye/spec
node --check screens_X.js
cat ui.js screens_X.js > /tmp/ck.js && node --check /tmp/ck.js
node -e "$(cat ui.js screens_X.js); let bad=SHOTS.filter(s=>{try{return !s.render()||s.render().length<80}catch(e){return true}}); console.log('shots',SHOTS.length,'bad',bad.map(s=>s.id))"
```

交回内容：`张数 / 自检结果 / 改了什么 / 踩了什么坑（供总工程师写回你的档案）`。
不要写诊断、不要提方案、不要改其它文件。
