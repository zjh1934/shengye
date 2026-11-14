package com.shengye.core.data

import com.shengye.contract.ProduceConfig
import com.shengye.contract.ProduceTask
import com.shengye.contract.ResumeReason
import com.shengye.contract.SubPhase
import com.shengye.contract.TaskState
import org.json.JSONArray
import org.json.JSONObject

/** tasks/<id>.json 的编解码。手写，坏了能用文本编辑器打开看，见 STORAGE.md。 */
internal object TaskJson {

    fun encode(t: ProduceTask): String {
        val o = JSONObject()
        o.put("id", t.id)
        o.put("bookId", t.bookId)
        o.put("bookTitle", t.bookTitle)
        o.put("state", t.state.name)
        o.put("createdAt", t.createdAt)
        o.put("chunksDone", t.chunksDone)
        o.put("chunksTotal", t.chunksTotal)
        val ch = JSONArray()
        t.chapterIndexes.forEach { ch.put(it) }
        o.put("chapters", ch)
        o.put("subPhase", t.subPhase?.name ?: "")
        o.put("resumeReason", t.resumeReason?.name ?: "")
        val cfg = JSONObject()
        cfg.put("readingStyle", t.config.readingStyle)
        cfg.put("pace", t.config.pace)
        cfg.put("intensity", t.config.intensity)
        cfg.put("silence", t.config.segmentSilenceMs)
        cfg.put("autoJoin", t.config.autoJoinChapters)
        cfg.put("seam", t.config.seamHandling)
        cfg.put("format", t.config.outputFormat)
        cfg.put("sampleRate", t.config.sampleRate)
        cfg.put("bitrate", t.config.bitrateKbps)
        cfg.put("channels", t.config.channels)
        o.put("config", cfg)
        return o.toString()
    }

    fun decode(text: String): ProduceTask? = runCatching {
        val o = JSONObject(text)
        val ch = o.optJSONArray("chapters") ?: JSONArray()
        val chapters = (0 until ch.length()).map { ch.getInt(it) }
        val cfg = o.optJSONObject("config")
        ProduceTask(
            id = o.getString("id"),
            bookId = o.getString("bookId"),
            bookTitle = o.getString("bookTitle"),
            chapterIndexes = chapters,
            state = runCatching { TaskState.valueOf(o.getString("state")) }.getOrDefault(TaskState.QUEUED),
            config = ProduceConfig(
                readingStyle = cfg?.optString("readingStyle") ?: "",
                pace = cfg?.optString("pace") ?: "",
                intensity = cfg?.optString("intensity") ?: "",
                segmentSilenceMs = cfg?.optInt("silence") ?: 0,
                autoJoinChapters = cfg?.optBoolean("autoJoin") ?: false,
                seamHandling = cfg?.optBoolean("seam") ?: false,
                outputFormat = cfg?.optString("format") ?: "m4a",
                sampleRate = cfg?.optInt("sampleRate") ?: 24000,
                bitrateKbps = cfg?.optInt("bitrate") ?: 64,
                channels = cfg?.optInt("channels") ?: 1,
            ),
            createdAt = o.optLong("createdAt"),
            chunksDone = o.optInt("chunksDone"),
            chunksTotal = o.optInt("chunksTotal"),
            subPhase = o.optString("subPhase").takeIf { it.isNotBlank() }
                ?.let { runCatching { SubPhase.valueOf(it) }.getOrNull() },
            resumeReason = o.optString("resumeReason").takeIf { it.isNotBlank() }
                ?.let { runCatching { ResumeReason.valueOf(it) }.getOrNull() },
        )
    }.getOrNull()
}
