# 02 · 进度看板（大家一起看的唯一真源）

> 主对话每次会话进门必读本文件；每完成一件事**立即写回**，不靠记忆。
> 子智能体交回成果后，由主对话验收并更新本表；子智能体不直接改本文件。

## 阶段路线

| 阶段 | 目标 | 状态 |
|---|---|---|
| **P0 地基** | 防失忆文档、分工制度、模块化契约、口味宪法、功能保留清单 | ✅ 完成（10 份文档） |
| **P1 方向** | 苹果毛玻璃主题 + 不啰嗦 + 四空间职责分开 + 调音台玻璃化，可交互原型 | ✅ **已确认**（用户授权「你看着推进就行，你自己定」） |
| **P2 骨架** | 新 Android 工程、模块分包、`:core:contract` 接口、状态词表、玻璃 token、界面规格全集 | ✅ **完成**（工程骨架 + 契约 + 65 张界面规格全部验收） |
| **P3 逐模块落地** | 按角色派活，逐个 feature / core 实现 + 单测，主对话验收 | 🔵 **进行中**（domain / glass / data 已落，首包已发） |
| **P4 集成** | 组装、真机手感回归、CI、发版口径 | ⬜ 未开始 |

## 仓库与交付通道（★ 换机器/换会话第一件事看这里）

| 项 | 值 |
|---|---|
| 仓库 | https://github.com/zjh1934/shengye （public） |
| 默认分支 | `main` |
| **推送方式** | **本机 git 协议上传不通**（TCP/TLS 能连上，数据发不出去，`git push` 卡死）→ 一律用 `tools/push_api.py` 走 GitHub REST API 建提交 |
| 推送命令 | `GH_TOKEN=xxx python3 tools/push_api.py`（默认取本地 HEAD 的 commit message） |
| CI | `.github/workflows/android.yml`，push → `assembleDebug` → artifact；打 `v*` tag → 自动发 Release |
| 用户取包 | **只从 Releases 页下载 APK**，不往沙盒下载安装包 |
| 已知 CI 坑 | `android-actions/setup-android@v3` 在本 runner 上必失败 → 已改为用预装 SDK 自补 `cmdline-tools`；Gradle 也改为官方发行包自装，减少第三方 action 变量 |

## 模块状态表

| 模块 | Owner 角色 | 状态 | 验收人 | 备注 |
|---|---|---|---|---|
| `00_CHARTER` / `01_TEAM` / `CONSTITUTION` / `contracts/ARCHITECTURE` | 总工程师 | ✅ 已定稿 | 主对话 | — |
| `roles/*` 档案 ×5 | 总工程师 | ✅ 已建立 | 主对话 | 每次派活随任务附上 |
| `preview/index.html` 毛玻璃原型 | 总工程师 | ✅ 已确认方向 | 主对话 | 代码绘制，非生图 |
| `contracts/STATE_VOCAB.md` 统一状态词表 | 总工程师 | ✅ 已定稿 | 主对话 | 消灭旧版 6 套叫法 |
| `contracts/GLASS_TOKENS.md` 视觉 token | 总工程师 | ✅ 已定稿 | 主对话 | 含 Android 8.0+ 降级策略 |
| `contracts/ACCEPTANCE.md` 验收清单 | 总工程师 | ✅ 已定稿 | 主对话 | 补上「真机手感回归」 |
| `spec/SPEC_CONTRACT.md` + `ui.js` + `shell.html` + `app.js` + `build.py` | 总工程师 | ✅ 已定稿并冒烟通过 | 主对话 | 子智能体的唯一共同语言 |
| `android/` 新 Gradle 多模块骨架（17 模块 + 53 文件） | 总工程师 | ✅ 已落地 | 主对话 | 本轮不接 SDK、不追求编译 |
| `android/core/contract` 4 个 Kotlin 文件 | 总工程师 | ✅ **已定稿** | 主对话 | 词表直接落进枚举，最敏感 |
| `spec/index.html` 重做后界面规格全集 | ui/audio/data | ✅ **65 张已验收** | 主对话 | 8 组 A–H，五道验收全过 |
| `design:glass` | ui | ✅ **已验收** | 主对话 | token / 主题 / 骨架 / 7 个通用展示组件，CI 编译通过 |
| `core:domain` | domain | ✅ **已验收** | 主对话 | 状态机 / 进度口径 / 分块 / 分章 / 路径规则，39 个单测全绿 |
| `core:audio` / `feature:player` | audio | ⬜ | 主对话 | — |
| `core:data` | data | ✅ **已验收** | 主对话 | 原子写 / book.json / 书库，编码三路探测 |
| `feature:models` / `platform:service` | data | ⬜ | 主对话 | — |
| 其余 `feature:*` | ui | ⬜ | 主对话 | — |
| `platform:tts` | 总工程师 | ⬜ | 主对话 | JNI，风险最高，亲自管 |
| CI → Release（`.github/workflows/android.yml`） | 总工程师 | ✅ **已跑通** | 主对话 | `v0.1.0-alpha1` 已发 Release，APK 8.4 MB 可下载 |

## 第一次实战派活（验证 `01_TEAM.md` 制度是否真跑得通）

> 用户问「多智能体怎么分工、会不会失忆、是不是黑盒」——这一批就是答案的实证。
> 三个后台子智能体**无长期记忆、对主对话黑盒**，每个任务说明都附了：对应 `roles/*.md` 档案 + `ARCHITECTURE` + `STATE_VOCAB` + `GLASS_TOKENS` + `CONSTITUTION` + `SPEC_CONTRACT` + 方向原型路径，并明确「只写自己那一个文件、交回后只验收产物」。

| 后台任务 | task_id（防失忆，压缩后靠它找） | 产物 | 组 / 张数 | 状态 |
|---|---|---|---|---|
| `spec-tasks-resume` | `agent-52a5bcf2-4ebc-4cb1-ae02-096c599cb3f7` | `screens_2.js` | C 10 + D 8 | ✅ 已验收（18 张，五道全过） |
| `spec-lib-a` | `agent-eef1b811-e494-45dc-91ee-a5ee7efd56bf` | `screens_1.js` | A 8 | ✅ 8 张已落盘并验收（A1/A5/A7 由主对话修正） |
| `spec-book-b` | `agent-2a22b675-7522-4f77-a93a-dbf2db695c26` | `screens_1b.js` | B 10 | ⚠️ **只交到 B8**（8/10），B9/B10 由主对话补在 `screens_1c.js`；B5/B6/B7 已由主对话修正 |
| ~~`spec-library-book`~~ | ~~`agent-9fd23e9f-…`~~ | —— | A+B 18 | ❌ 失败零落盘（`Empty stream`，548 万字节推理未写文件） |
| ~~`spec-player-mixer`~~ | ~~`agent-83134801-faf7-49c8-bdbb-590755ec95ac`~~ | —— | E+F+G+H 27 | ❌ **静默消失**：无通知、无文件、任务已不在；27 张单批过重 |
| **`spec-player-e`** | `agent-033a0e25-721a-473b-9869-b775185c293f` | `screens_3.js` | E 8 | ✅ 8 张已验收（播放器信息已补回） |
| **`spec-mixer-f`** | `agent-e92d823e-6115-4697-a979-ba516f0548e8` | `screens_3b.js` | F 6 | ✅ 6 张已验收（F4 文案由主对话收简） |
| **`spec-settings-g`** | `agent-b5df8332-4d50-4e60-afa2-fce2ace7e8f6` | `screens_4.js` | G 8 | ✅ 8 张已验收（G1 分组标题去重） |
| **H 组（主对话亲画）** | —— | `screens_5.js` | H 5 | ✅ 已出（通知去重已修） |

**图册现状**：**65 张 / 8 组全部验收通过**（A9 · B10 · C11 · D8 · E8 · F6 · G8 · H5）。零渲染失败、零重复 id、零缺字段、禁词 0、内联色值 0；唯一残留迷你条是 E5 的「播放中」显式迷你条（正确）。

**C/D 组验收记录（已做）**：`node --check` 单文件与全量拼接通过、18 张 render 全成功、无重复 id、禁词扫描为空、无内联色值；逐组看图确认 C 组每屏一根条一个百分比、块网格只在 C9、成品只在 C10 独立入口，D 组六张续跑按原因分卡（结构各异）、D7 是常驻可撤销恢复条、D8 是参数摘要确认卡而非说明书。踩坑已写回 `roles/ui.md` / `roles/domain.md`，失败教训已写进 `01_TEAM.md`「派活规模纪律」。

**收活动作（图齐后由主对话做，不问用户）**：`node --check` 全量 → `build.py` 重建 → 浏览器渲染零失败 → 逐组看图 → 对照 `ACCEPTANCE.md` B 组硬项与 `STATE_VOCAB.md` 逐字核对 → 统一改 `ui.js` 迷你条默认值并重建 → 写回本表 → 交付完整版。

## 待同步的契约微调（下一批派活前统一改，本批不动）

> 从 2 张基准样板图上看出来的，**不改当前 `ui.js`**：三个子智能体已按现有契约上工，契约稳定性优先于完美。

1. `P()` 默认带一条「未在播放」占位迷你条，与 `GLASS_TOKENS.md §4`「迷你播放条（有播放时）」不一致。**看图确认 C/D 组 18/18 张全带这条占位条**，是系统性噪音 → **最终收活时统一改 `ui.js`：`U.phone` 默认不补 mini，播放态显式传 `U.mini({playing:true})`**。这是纯兼容的默认值修正，不会让任何已交文件报错，故留到三组齐了一次改到位（现在不动，避免打扰还在上工的任务）。补派的两个任务已要求直接传 `mini:false`。
2. `U.bar({title:'X'})` 与 `U.hero('X')` 会造成同屏大标题重复 → 下批在 `SPEC_CONTRACT.md` 明令禁止，基准样板已先示范（去掉 hero、传 `mini:false`）。

## 用户走查采纳项（U1–U8，收活时统一修）

> 来源 `USER_JOURNEY.md`。三个真人（小白 / 老用户 / 回坑者）走完 10 条主流程后的判定，已由主对话拍板。

| # | 问题 | 落点 | 状态 |
|---|---|---|---|
| U1 | B5「接着听」4/6 却显示 100%，与 B1 的 66% 矛盾 | screens_1b.js B5 | ⬜ **必修**，待 B 组交回 |
| U2 | B6 阅读器缺 A-/A+ 字号调节（旧版设定被丢） | screens_1b.js B6 | ⬜ **必修**，待 B 组交回 |
| U3 | B7 重新分章未提「已制作音频会清掉」 | screens_1b.js B7 | ⬜ 采纳，待 B 组交回 |
| U4 | A7 片段与书混排、无分区标题 | screens_1.js A7 | ✅ 已修（加「片段 · 3」） |
| ~~U5~~ | ~~A8 删除确认缺「不可撤销」~~ | —— | ✅ **复核后已满足**（A8 本就有 `text:'不可撤销'`，此前看缩略图误判，已撤回） |
| U6 | A1 空态用状态词「未开始」当文案、未说支持格式 | screens_1.js A1 | ✅ 已修（「还没有书 · 支持 txt / md / epub」+ 垂直居中） |
| U7 | D8 缺「更多参数 ›」（段间静音 / 接缝 / 自动拼章 / **从头重做**） | screens_2.js D8 | ✅ 已修 |
| U8 | 调音台入口只在「正在做」 | screens_2/3 | ⬜ 采纳，待 E/F/G/H 交回时补 |
| **U9** | **导入中的书挂了「制作中」**——导入不是制作，词表缺导入态 | screens_1.js A5 + `STATE_VOCAB.md §1.5` | ✅ 已修（新增导入中 / 导入失败 / 已导入） |

**明确不采纳**：新手引导、删除连锁后果清单、首页四件套、定时关闭 / 均衡器、模型页结构重做。理由见 `USER_JOURNEY.md`。

## 变更记录

- 2026-11-15：**决议重写**（不在旧码上改），建立防失忆 / 分工 / 契约 / 宪法 / 进度五套地基文档 + 5 份角色档案 + 毛玻璃方向原型。旧码 `/workspace/tingben` 转为只读比对源。
- 2026-11-15：用户确认方向并授权自主推进。进入 **P2**：新建状态词表 / 视觉 token / 验收清单三份契约；起新 Gradle 多模块骨架（17 模块）；亲自定稿 `:core:contract`（数据模型 / 状态枚举与文案 / 7 个仓库接口 / UiIntent）；搭建界面规格绘制链路并冒烟通过；**首次按分工制度派出 3 个后台子智能体**画 63 张重做后形态图。
- 2026-11-15（续）：C/D 组 18 张交回并**通过五道验收**（语法 / 渲染 / 禁词 / 重复 id / 逐张看图）；A/B 组首个任务**黑盒失败、零落盘**，按「缩小范围 + 强制先落盘」拆成 8 + 10 两张补派。踩坑写回 `roles/ui.md` / `roles/domain.md`，失败教训固化为 `01_TEAM.md`「派活规模纪律」。看图发现 `ui.js` 默认迷你条造成 18/18 占位噪音，登记为最终收活时统一修正。渲染层 `match()` 大小写已加固。
- 2026-11-15（续二）：按用户要求补做**真人走查**（小白 / 老用户 / 回坑者 × 10 条主流程），产出 `USER_JOURNEY.md`，拍板 U1–U8 采纳项与 5 条明确不采纳项；修正自己过严的契约（首页允许单本一根进度，禁的是双口径四件套）。已落地：`ui.js` 默认不再补占位条（36/36 张已干净）、手机改 flex 布局（无底部空隙）、C9 子阶段改为一屏一个。A 组 8 张、B 组 8/10 已落盘，E/F/G/H 组仍在跑。
- 2026-11-15（续三）：E/F/G/H 那个 27 张任务**静默消失**（无完成通知、无文件、任务已不在），属于第二批黑盒失效。按「派活规模纪律」拆成 **E 8 / F 6 / G 8 三路重派**，H 5 张由主对话亲画（系统态是样板，基准该我定）。三路首次检查即已分批落盘（E2 / F2 / G6），零落盘风险消除。主对话本轮修正：A1 空态文案（状态词当文案）→ 人话 + 格式 + 居中；A5 导入中误挂「制作中」→ 新增 `STATE_VOCAB §1.5` 导入态；A7 加「片段」分区；B5「4/6 却 100%」→ 66%；B6 字号 `slider` 语义错用 → A-/A+；B7 补「已制作的音频会清掉」；D8 补「更多参数 ›」；H 通知标题去重；`android/core/domain` 包路径修正。图册 46 张 / 8 组。

- 2026-11-15（换大脑）：**主对话模型由 DeepSeek 切换为 Kimi**。用户原话：「前面的对话都是 deepseek 做的，现在你是 kimi 在 workbuddy 上运行，我嫌前面那个模型做的很拉垮，你们共享了上下文，现在是换了一个大脑」。**已有成果全部保留、不做回退**（我一度误把「撤掉 DeepSeek 做的」理解成撤子智能体产物，被用户纠正：子智能体的活没问题，要撤的是主模型犯浑的部分，已当场恢复 65 张图）。

- 2026-11-15（真机验收 alpha1）：用户装上后提三条，全部落代码并出 alpha2——
  1. **输入框太大** → 150dp 最小高 + weight 撑满，改固定 72dp
  2. **没考虑状态栏和底部导航栏** → 新增 `design:glass/Insets.kt`：`GlassScaffold` 统一管系统栏，
     顶栏吃 statusBars、tab 底色铺到屏幕最底且内容避让手势条、横屏刘海只处理一次、
     状态栏/导航栏图标黑白跟随主题；`enableEdgeToEdge` + 刘海 shortEdges + adjustResize + 深色启动底色
  3. **首页废话多** → 删「我的书」大标题（tab 已写）、删「粘贴一段文字」标题（placeholder 已说明）、
     删书单分组标题与重复导入入口；输入框与两个按钮收进同一张卡
  **新工程规矩 `contracts/PREVIEW.md`**：任何界面必须先渲真机尺寸（`tools/shot.py`）、自己看图再提交，
  附系统栏/废话/比例/状态四组检查表。这是用户明确要求的「整个工程都这样」。
  同期接通**第一个能真点动的功能**：导入 txt（SAF）与粘贴文本 → 书单；
  `core:data` 新增 `importBytes` / `importText` 统一入口。

## P3 批次计划（谁做什么、产物是什么）

> 原则：**核心逻辑与平台相关的自己做，界面组装派出去**。
> 每批产物必须能单独编译、单独验收，批与批之间不互相等。

| 批次 | 内容 | 谁做 | 产物 | 状态 |
|---|---|---|---|---|
| P3-1 | 制作状态机 / 进度口径 / 分块 / 分章 / 路径规则 + 单测 | 主对话 | `core:domain` 6 文件 + 39 单测 | ✅ |
| P3-2 | 毛玻璃 token / 主题 / 骨架 / 通用展示组件 | 主对话 | `design:glass` 4 文件 | ✅ |
| P3-3 | 存储层：书库读写已落，任务/成品/模型清单待补 | 主对话 | `core:data` | 🔵 部分 |
| P3-4 | 前台服务 + 保活梯级 + 通知 + 音频合成流水线 | 主对话 | `platform:service` + `core:audio` | ⬜ |
| P3-5 | 端侧 TTS JNI 桥 | 主对话 | `platform:tts` | ⬜ |
| P3-6 | 我的书（含首页 1/3 粘贴区） | 派 `ui` | `feature:library` | ⬜ |
| P3-7 | 这一本（目录 / 阅读器 / 勾章） | 派 `ui` | `feature:book` | ⬜ |
| P3-8 | 正在做（任务卡 / 块网格 / 参数摘要） | 派 `ui` | `feature:tasks` | ⬜ |
| P3-9 | 正在听（播放器全屏） | 派 `audio` | `feature:player` | ⬜ |
| P3-10 | 调音台 | 派 `audio` | `feature:mixer` | ⬜ |
| P3-11 | 设置 + 模型 | 派 `data` | `feature:settings` `feature:models` | ⬜ |
| P3-12 | 组装、真机手感回归、打首个 `v*` tag | 主对话 | Release 页可下载 | ⬜ |

**派活只派 P3-6 到 P3-11。** 每个任务说明必附：对应 `roles/*.md`、`contracts/PITFALLS.md`、`GLASS_TOKENS.md`、`STATE_VOCAB.md`、`spec/screens_*.js` 里对应那一组图、以及「只准写你负责的那一个 feature 目录」。

- 2026-11-15（续四）：三路重派全部交回，**65 张 8 组齐**。全量验收：零渲染失败 / 零重复 id / 零缺字段 / 禁词 0 / 内联色值 0。逐组看图结论：播放器 E 组已补回封面·章节归属·全书位置·全本剩余·队列；调音台 F 组结构保留、旧版解释句已清零；设置 G 组完成重分层、模型只留名称体积状态动作。主对话收尾修正 F4 文案与 G1 分组标题。P2 完成。

- 2026-11-15（续五）：**改名决议**——「听本」→ **声页 Shengye**；包名 `com.tingben.app` → `com.shengye.app`；目录 `/workspace/tingben-rebuild` → `/workspace/shengye`；仓库名 `shengye`（public）。旧码仍为 `/workspace/tingben`，未动。首页按用户要求改版：**顶部约 1/3 屏固定「粘贴文本 → 合成」**，其余给书单（契约已同步 CONSTITUTION / SPEC_CONTRACT）。新增 `contracts/BACKGROUND_KEEPALIVE.md`（L1–L6 保活梯级、权限清单、六厂商自启动路径、五条红线）。工程已可编译：`MainActivity` 骨架 + Manifest（**无 INTERNET**）+ 自适应图标 + 主题 + Compose 依赖；CI 配好 `assembleDebug` → artifact → tag 发 Release。本地 `git init` 并提交 95 个文件。**GitHub 建仓被拒**：MCP token `403 Resource not accessible by integration`，需用户重新连接连接器或提供 PAT。归因修正：子智能体失败主因是**免费额度耗尽**、不是任务规模（`01_TEAM.md` 已改写）。

- 2026-11-16：**仓库落地**。`gh repo create zjh1934/shengye --public` 成功；但 `git push` 卡死（TCP/TLS 能连、数据发不出去）→ 改用 GitHub REST API 逐 blob 建提交，96 文件一次推上去（`2fc98715`）。固化 `tools/push_api.py`（幂等：内容没变就跳过；parent 取远端 main，线性前进不 force）。CI 首次运行失败在 `android-actions/setup-android@v3`，改为用 runner 预装 SDK 自补 `cmdline-tools`、Gradle 也改官方发行包自装，并把 `:core:domain:test` 排在 assembleDebug 之前。
  **P3 开工**：`core:domain` 落地 4 个纯逻辑文件——`ProduceMachine`（状态机，非法转移一律原地返回、暂停/停止期间迟到回调不推进进度）、`Progress`（**唯一进度口径**，pct + 一行运行态 + ETA）、`Chunker`（确定性分块，块边界必须稳定否则续跑错位）、`ResumePolicy`（6 种续跑原因判定，明确「哪些该弹窗、哪些只给常驻条」）+ 24 个单测。这块是本项目最容易出 bug 的地方，主对话亲自写，不外包。

- 2026-11-16（续）：**CI 跑通并能发版**。修掉五道连环坑：`native` 是 Java 关键字（模块改名 `:platform:tts`）、`AppState.kt` 注释里的 `/*` 触发嵌套注释吞掉整个文件、`colors.xml` 注释含 `--`、11 个模块的 Java 1.8 与 Kotlin 17 target 不一致、`storedChunks` 表达式体带 `return`。全部写进 `contracts/PITFALLS.md`（现有 11 条，派活必附）。
  **新增落地**：`design:glass` 4 文件（token / 主题 / 骨架 / 7 个通用展示组件）、`core:domain` 再加 `StorageLayout` + `ChapterSplitter`（+15 单测，共 39）、`core:data`（原子写 / book.json 编解码 / 书库，含 UTF-8·BOM·GBK 三路编码探测）、`contracts/STORAGE.md`。
  **首包发布**：打 `v0.1.0-alpha1` → Actions 自动发 Release，`shengye-9e4d681.apk` 8.4 MB 已挂上。**这个包只有骨架**（四空间 tab + 首页粘贴区 + 毛玻璃），导入 / 合成 / 播放还没接，发它是为了真机确认毛玻璃观感，方向不对趁早改。
  CI 现在：单测 → assembleDebug → artifact → tag 发 Release，全程约 2 分钟（已加 Gradle 缓存）。
