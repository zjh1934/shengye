# · 模块化契约（怎么做到不牵一发而动全身）

> 旧版病根：83 个 `.kt` 全堆在 `com.tingben.app` 一个包；`MainActivity` 一个巨型 Composable 承载导航、抽屉、播放条、播放器、调音台、续跑弹窗；页面里 `remember{}` 直接读盘、每秒轮询。任何改动都会牵动全局。
> 重写用**按"变更原因"分包**替代按技术层堆文件，并让**模块边界 = 角色边界 = 档案边界**。

## 一、模块表

```
:core:contract      纯 Kotlin：数据模型 + 接口。无 Android 依赖。唯一被所有人依赖的契约层
:core:domain        合成状态机：分章 / 小块计划 / 断点续跑 / 任务。纯逻辑，可单测
:core:audio         拼接 / 转码 / 校验 / WAV 清理 / 章名蒙层
:core:data          书 / 成品 / 模型 / 偏好 / 位置 的读写与 IO

:design:glass       毛玻璃设计系统：颜色 / 模糊 / 圆角 / 字重 / 组件 / 深浅色。唯一管视觉的地方

:feature:library    我的书（首页）
:feature:book       这一本（目录页，只管内容与章节）
:feature:tasks      正在做（全 App 唯一状态中心）
:feature:player     正在听（播放器 + 队列）
:feature:mixer      调音台（保留）
:feature:settings   设置
:feature:models     模型（小修）

:platform:service   前台服务 / 通知 / 悬浮球
:platform:native    JNI / 端侧 TTS 引擎
:app                组装：导航 + 依赖注入。只接线，不承载业务逻辑
```

## 二、四条硬规则

1. **依赖单向**
   `feature → core:contract / design:glass`；`core:*` 之间只准依赖 `core:contract`；`platform:*` 只依赖 `core:contract`；`app` 依赖全部。
   **任何模块不得反向依赖 `app` 或另一个 `feature`。** 违反即验收打回。

2. **状态唯一来源**
   一个 `AppRepository / AppState` 聚合层持有运行状态；页面**只读状态 + 发意图（Intent）**，不准自己读盘、不准自己轮询、不准自己持有第二份进度。`feature:book` 与 `feature:tasks` 从同一处取数、用同一套措辞——直接消灭旧版"同一份状态两套说法"。

3. **视觉唯一来源**
   颜色 / 模糊 / 圆角 / 字重 / 间距全在 `design:glass` 的 token 里，页面不写死任何色值。换主题只改一处。

4. **契约先行**
   模块之间只通过 `core:contract` 的接口通信。接口签名变更 → 必须过总工程师验收，并同步更新受影响角色的档案。

## 三、为什么这样就不会"牵一发动全身"

- 改视觉 → 只动 `design:glass`，feature 层零改动。
- 改合成算法 → 只动 `core:domain` / `core:audio`，UI 零改动。
- 改一个页面 → 只动那一个 `feature:*`，其余页面零改动。
- 每个改动都落在**一个模块 + 一份档案 + 一个角色**里，派活时不必把整个项目塞进上下文。

## 四、毛玻璃的落地现实（不骗自己）

- 苹果式 backdrop blur 在 Android 上依赖硬件：`RenderEffect`（12+）/ `Haze` 类方案。
- 本项目要保 **Android 8.0+**，低端机可能不支持。故 `design:glass` 必须内置**降级策略**：不支持时退化为「半透明纯色 + 细边 + 柔和阴影」，层级不丢、功能不残。
- 判据：**毛玻璃是增强，不是前置条件**；没有模糊也要能用、要好看。

## 五、防"牵一发动全身"的验收项

- [ ] 任一 `feature:*` 不 import 另一个 `feature:*`
- [ ] 任一页面无文件 IO、无 `while(delay)` 轮询
- [ ] 页面内无硬编码色值 / 圆角 / 字重
- [ ] 状态词表唯一（一处定义）
- [ ] 删掉某一个 `feature:*`，`:app` 之外仍能编译
