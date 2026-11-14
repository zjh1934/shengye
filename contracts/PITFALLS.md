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

## 10. 新建 Android 模块忘了对齐 JVM target

**现象**：`Inconsistent JVM-target compatibility detected for tasks 'compileDebugJavaWithJavac' (1.8) and 'compileDebugKotlin' (17)`。

**根因**：Kotlin 插件默认按 toolchain 出 17，Java 编译默认还是 1.8。两者不一致，AGP 直接拒绝。

**正确做法**：每个 `com.android.library` / `application` 模块的 `android { }` 里都要有：

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
```

新建模块照抄这份，别只写 `namespace` 和 `compileSdk` 就跑。

## 11. 中文正则里别加 `\b`

**现象**：分章正则写了 `第X章\b`，结果一章都识别不出来。

**根因**：Java 正则的 `\b` 基于 `\w`（字母数字下划线），中文字符不算 `\w`，所以「章」和后面的空格之间**没有**词边界，`\b` 永远不成立。

**正确做法**：中文匹配不要用 `\b`，靠 `^` 行首 + 明确的字符类来限定。

## 12. `windowInsetsPadding()` 是 @Composable

**现象**：写了个普通 `Modifier.statusBarInset()` 扩展，里面调 `windowInsetsPadding`，编译报
"Functions which invoke @Composable functions must be marked with the @Composable annotation"。

**根因**：`Modifier.windowInsetsPadding(insets)` 带 `@Composable` 注解，只能在 Composable 上下文里调。
`Modifier.statusBarsPadding()` 这类便捷扩展同理。

**正确做法**：包装它的 Modifier 扩展自己也标 `@Composable`。Compose 里"只是个 Modifier 链"
不代表能脱离 Composable 上下文。

## 13. SAF 选文件拿不到路径，粘贴区拿不到文件

**现象**：`ActivityResultContracts.OpenDocument()` 返回的是 `content://` URI，
`File(uri.path)` 读出来是空的；粘贴区干脆连文件都没有。

**正确做法**：仓储层统一收 `ByteArray`（`importBytes(name, bytes)`），
另给粘贴区一个 `importText(text)`。解码、分章、指纹只写一份，三条路共用。
不要让 app 层把 content 流先落临时文件再传路径——那是绕路。


## 14. 自检脚本会把 MIME 字符串里的 `/*` 误报成注释

**现象**：跑「Kotlin 块注释配对」自检，`MainActivity.kt` 报 `/*=3 */=2`，
看着像又踩了第 1 条的嵌套注释坑。

**根因**：`pick.launch(arrayOf("text/*"))` 里那个 `/*` 是 MIME 类型字符串的内容，
编译器按词法先识别字符串，不会当成注释。纯文本计数脚本分不清。

**正确做法**：看到配对不等于别急着改代码，先 `grep -n '/\*\|\*/'` 看命中在哪一行。
在字符串里就是假阳性。真正危险的是**注释正文**里出现 `/*`（第 1 条）。
另外纯 JVM 模块（`java-library` + `kotlin.jvm`）用 `jvmToolchain(17)` 已经统一，
不需要 Android 的 `compileOptions`，「缺 jvmTarget」对它们也是误报。

## 15. 设计态假数据不能画成真机样子

**现象**：规格 B 组画的是「8:12 时长 / 可听 4/6 / 制作中」，但真机上刚导入的书
全是「未开始」、只有字数、没有任何时长；勾章时块数也还没算出来。

**根因**：形态图为了表达「有数据时长什么样」用了假态。照着它写 UI，
就会显示一堆不存在的音频时长，等于说假话。

**正确做法**：`spec/` 是结构参考，`preview/real/` 是「做出来长什么样」，
按真实数据态画（`PREVIEW.md` 第 4 节）。本章真机图用「字数 / 0 章可听 / 未开始」，
勾章条只写「已选 N 章」不写块数，任务卡排队中不画 0% 空进度条。
**状态必须说真话**：引擎没接就停 QUEUED，不写 RUNNING，不假装合成成功。

## 16. 条件分支里的 Composable lambda 显式声明类型

**现象**：`topBar = if (cond) { { GlassBackBar(...) } } else null` 这种写法，
lambda 的 `@Composable` 推断在条件表达式里可能丢失，编译报类型不匹配。

**正确做法**：先声明带类型的局部变量再传：

```kotlin
val bar: (@Composable () -> Unit)? = if (cond) { { GlassBackBar(...) } } else null
GlassScaffold(topBar = bar, ...)
```

`Glass.colors` 是 `@Composable get()`（读 CompositionLocal），
不能在普通 `map { }` 里取；要在 Composable 上下文或 Composable lambda 里取。
本轮 `TaskRow` 的进度全用纯逻辑 `Progress` 在 app 层算好，feature 不碰它。


## 17. 表达式体函数里不能写 `return`

**现象**：`private fun read(id: String): T? = TaskJson.decode(... ?: return null)` 编译报
`Returns are prohibited for functions with an expression body. Use block body '{...}'`。
本轮 `FileTaskRepository.read()` 又踩了一次——之前 `FileBookRepository.storedChunks()`
也踩过同一个，说明光修一处不够。

**根因**：`fun f() = expr` 的等号后面只能是**表达式**，不能是语句，`return` 是语句。
带标签的 `return@io` / `return@mapNotNull` 在 lambda 里合法，裸 `return` 不行。

**正确做法**：改成块体。

```kotlin
private fun read(taskId: String): ProduceTask? {
    val text = Files2.readTextOrNull(...) ?: return null
    return TaskJson.decode(text)
}
```

**自查**：全仓列出所有裸 `return` 行，看上文函数是不是 `= ` 开头而不是 `{`：
`python3` 扫 `*.kt` 即可，比等 CI 报快得多。


## 18. 骨架里的空 feature 模块都没装 Compose 编译器插件

**现象**：往 `feature:book` / `feature:tasks` 第一次写 `@Composable` 代码，编译挂在一句
`BackendException: Exception during IR lowering` +
`couldn't find inline method ColumnKt.Column$default(...)`。日志里**没有** `e: file:...`
这种常规语法错误，全是 Kotlin 后端内部栈，很容易误判成环境问题。

**根因**：P2 建骨架时只有 `app` / `design:glass` / `feature:library` 装了
`id("org.jetbrains.kotlin.plugin.compose")` + `buildFeatures { compose = true }`；
其余 7 个 feature 模块是空的，没装。Kotlin 2.0 起 Compose 编译器由该插件提供，
没插件的模块里 `@Composable` 函数不做 IR 变换，内联 `Column` 时找不到 `$default` 桥。

**正确做法**：**落码前一次性给所有 `feature:*` 模块补齐**，别等谁先写页面谁先炸：

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android { ...; buildFeatures { compose = true } }
```

`core:*` 是纯逻辑（java-library），`platform:service` / `platform:tts` 是系统层，
都不该引 Compose，别一起加。看到 `IR lowering` / `couldn't find inline method`
先查插件，不要去改页面代码。
