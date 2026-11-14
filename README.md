# 声页 Shengye

本地离线把 `txt / md / epub` 变成能听的音频 · 端侧 TTS · **无网络权限**。

> 2.0 重写工程。旧版（听本 1.1.43）只读保留作功能比对源，不在其上修改。

## 下载

从 [Releases](../../releases) 页下载 APK（CI 在打 tag 时自动发布）。需要 Android 8.0+ / arm64-v8a。

## 现在到哪了

| 阶段 | 内容 | 状态 |
|---|---|---|
| P0 地基 | 防失忆文档、分工制度、口味宪法、模块化契约 | ✅ |
| P1 方向 | 苹果毛玻璃 + 不啰嗦 + 四空间职责分开 | ✅ |
| P2 骨架 | 17 模块工程、`:core:contract`、状态词表、65 张界面规格、CI→Release | ✅ |
| P3 逐模块落地 | `core:domain` → `design:glass` → 各 feature | 🔄 |
| P4 集成 | 真机手感回归、正式签名、发版 | ⬜ |

## 文档入口（按顺序读）

| 文件 | 作用 |
|---|---|
| `00_CHARTER.md` | 项目真源 / 进门第一份 |
| `02_PROGRESS.md` | 进度看板（唯一真源） |
| `CONSTITUTION.md` | 口味宪法 + 功能保留清单 |
| `USER_JOURNEY.md` | 真人走查（小白 / 老用户 / 回坑者） |
| `contracts/ARCHITECTURE.md` | 模块依赖规则 |
| `contracts/STATE_VOCAB.md` | 对外唯一状态词表 |
| `contracts/GLASS_TOKENS.md` | 毛玻璃 token + 降级 |
| `contracts/BACKGROUND_KEEPALIVE.md` | 保活与权限 |
| `contracts/ACCEPTANCE.md` | 验收清单 |
| `spec/index.html` | 65 张代码绘制的界面规格 |

## 构建

```bash
cd android
gradle assembleDebug
```

CI 见 `.github/workflows/android.yml`：push / PR 出 artifact，打 `v*` tag 自动发 Release。

## 红线

- 不声明 `INTERNET`（离线）
- 不做双进程守护 / 相互唤醒 / 滥用无障碍
- 界面不写解释型教程文字（运行状态必须有）
