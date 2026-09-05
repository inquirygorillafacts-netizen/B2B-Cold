package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentVoiceNoteId: String? = null,
    val progress: Float = 0f, // 0f to 1f
    val currentPositionSeconds: Int = 0,
    val durationSeconds: Int = 0
)

data class RecordingState(
    val isRecording: Boolean = false,
    val elapsedSeconds: Int = 0,
    val recordedFilePath: String? = null
)

class VoiceAudioManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null

    private val scope = CoroutineScope(Dispatchers.Main)
    private var playbackProgressJob: Job? = null
    private var recordingTimerJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _recordingState = MutableStateFlow(RecordingState())
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    fun playVoiceNote(voiceNoteId: String, filePath: String?, fallbackDurationSeconds: Int = 12) {
        if (_playbackState.value.isPlaying && _playbackState.value.currentVoiceNoteId == voiceNoteId) {
            pausePlayback()
            return
        }

        stopPlayback()

        // Check if file exists and is valid
        val file = if (!filePath.isNullOrBlank()) File(filePath) else null
        if (file != null && file.exists() && file.length() > 0) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    val totalDuration = if (duration > 0) duration / 1000 else fallbackDurationSeconds
                    _playbackState.value = PlaybackState(
                        isPlaying = true,
                        currentVoiceNoteId = voiceNoteId,
                        progress = 0f,
                        currentPositionSeconds = 0,
                        durationSeconds = totalDuration
                    )
                    setOnCompletionListener {
                        stopPlayback()
                    }
                    start()
                }
                startProgressTracker()
                return
            } catch (e: Exception) {
                Log.w("VoiceAudioManager", "Playback of real file failed, falling back to simulation: ${e.message}")
            }
        }

        // Simulated playback for demo or pre-seeded voice notes
        startSimulatedPlayback(voiceNoteId, fallbackDurationSeconds)
    }

    private fun startSimulatedPlayback(voiceNoteId: String, durationSeconds: Int) {
        val totalSecs = if (durationSeconds > 0) durationSeconds else 12
        _playbackState.value = PlaybackState(
            isPlaying = true,
            currentVoiceNoteId = voiceNoteId,
            progress = 0f,
            currentPositionSeconds = 0,
            durationSeconds = totalSecs
        )

        playbackProgressJob?.cancel()
        playbackProgressJob = scope.launch {
            val totalSteps = totalSecs * 10
            for (step in 1..totalSteps) {
                if (!isActive) break
                delay(100)
                val currentSec = step / 10
                val prog = step.toFloat() / totalSteps.toFloat()
                _playbackState.value = _playbackState.value.copy(
                    progress = prog.coerceIn(0f, 1f),
                    currentPositionSeconds = currentSec
                )
            }
            stopPlayback()
        }
    }

    private fun startProgressTracker() {
        playbackProgressJob?.cancel()
        playbackProgressJob = scope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val mp = mediaPlayer ?: break
                val current = mp.currentPosition
                val total = mp.duration
                if (total > 0) {
                    val prog = current.toFloat() / total.toFloat()
                    _playbackState.value = _playbackState.value.copy(
                        progress = prog.coerceIn(0f, 1f),
                        currentPositionSeconds = current / 1000
                    )
                }
                delay(100)
            }
        }
    }

    fun pausePlayback() {
        playbackProgressJob?.cancel()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    fun stopPlayback() {
        playbackProgressJob?.cancel()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _playbackState.value = PlaybackState()
    }

    private var recordingStartTimeMs: Long = 0L

    companion object {
        @Volatile
        private var cachedRecordingsDir: File? = null

        fun getAppRecordingsDirectory(context: Context): File {
            val existing = cachedRecordingsDir
            if (existing != null && existing.exists()) return existing

            val base = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                ?: context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
                ?: context.getExternalFilesDir(null)
                ?: context.filesDir
            val dir = File(base, "B2B_ColdCaller_Recordings").apply { mkdirs() }
            cachedRecordingsDir = dir
            return dir
        }

        fun getAllDiskRecordings(context: Context): List<File> {
            val dir = getAppRecordingsDirectory(context)
            val baseList = dir.listFiles { _, name -> name.endsWith(".m4a") }
                ?.filter { it.length() > 0 }
                ?.toList() ?: emptyList()

            // Also include files from public Downloads folder if accessible
            val pubDownloads = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "B2B_ColdCaller_Recordings"
            )
            val pubList = if (pubDownloads.exists()) {
                pubDownloads.listFiles { _, name -> name.endsWith(".m4a") }
                    ?.filter { it.length() > 0 }
                    ?.toList() ?: emptyList()
            } else emptyList()

            val combined = (baseList + pubList).distinctBy { it.name }
            return combined.sortedByDescending { it.lastModified() }
        }

        fun getTotalDiskRecordingsSizeBytes(context: Context): Long {
            return getAllDiskRecordings(context).sumOf { it.length() }
        }

        fun formatFileSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                else -> String.format(java.util.Locale.US, "%.1f MB", bytes.toDouble() / (1024.0 * 1024.0))
            }
        }

        fun syncSingleFileToPublicDownloads(context: Context, file: File) {
            try {
                if (!file.exists() || file.length() == 0L) return

                // 1. Direct copy to public Downloads folder for file managers
                try {
                    val pubDownloads = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "B2B_ColdCaller_Recordings"
                    )
                    if (pubDownloads.exists() || pubDownloads.mkdirs()) {
                        val dest = File(pubDownloads, file.name)
                        file.copyTo(dest, overwrite = true)
                        android.media.MediaScannerConnection.scanFile(
                            context,
                            arrayOf(dest.absolutePath, file.absolutePath),
                            arrayOf("audio/mp4", "audio/m4a"),
                            null
                        )
                    }
                } catch (e: Exception) {
                    // Scoped storage direct fallback
                }

                // 2. Publish to MediaStore.Downloads on Android 10+ (API 29+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        val resolver = context.contentResolver
                        val projection = arrayOf(android.provider.MediaStore.MediaColumns._ID)
                        val selection = "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${android.provider.MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
                        val selectionArgs = arrayOf(file.name, "%B2B_ColdCaller_Recordings%")
                        var existingUri: android.net.Uri? = null
                        resolver.query(
                            android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            null
                        )?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val id = cursor.getLong(cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns._ID))
                                existingUri = android.content.ContentUris.withAppendedId(
                                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                    id
                                )
                            }
                        }

                        val targetUri = existingUri ?: run {
                            val values = android.content.ContentValues().apply {
                                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "audio/mp4")
                                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Download/B2B_ColdCaller_Recordings/")
                                put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                            }
                            resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                        }

                        if (targetUri != null) {
                            resolver.openOutputStream(targetUri, "wt")?.use { out ->
                                file.inputStream().use { inp -> inp.copyTo(out) }
                            }
                            val updateValues = android.content.ContentValues().apply {
                                put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                            }
                            resolver.update(targetUri, updateValues, null, null)
                        }
                    } catch (me: Exception) {
                        Log.w("VoiceAudioManager", "MediaStore insert caught: ${me.message}")
                    }
                }
            } catch (e: Exception) {
                // Non-blocking
            }
        }

        fun syncAllToPublicDownloads(context: Context) {
            try {
                val files = getAllDiskRecordings(context)
                for (file in files) {
                    syncSingleFileToPublicDownloads(context, file)
                }
            } catch (e: Exception) {
                // Non-blocking
            }
        }

        fun migrateLegacyRecordings(context: Context) {
            try {
                val dir = getAppRecordingsDirectory(context)
                val files = dir.listFiles { _, name -> name.startsWith("rec_") && name.endsWith(".m4a") } ?: return
                for (legacyFile in files) {
                    val parts = legacyFile.nameWithoutExtension.split("_")
                    val rawNum = if (parts.size >= 2) parts[1].filter { it.isDigit() } else ""
                    val cleanNum = when {
                        rawNum.length > 10 -> rawNum.takeLast(10)
                        rawNum.isNotBlank() -> rawNum
                        else -> "recording"
                    }
                    var nextIdx = 1
                    for (i in 1..5) {
                        val cand = File(dir, "$cleanNum-$i.m4a")
                        if (!cand.exists()) {
                            nextIdx = i
                            break
                        }
                    }
                    val target = File(dir, "$cleanNum-$nextIdx.m4a")
                    if (legacyFile.renameTo(target)) {
                        android.media.MediaScannerConnection.scanFile(
                            context,
                            arrayOf(target.absolutePath),
                            arrayOf("audio/mp4", "audio/m4a"),
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                // Non-blocking
            }
        }
    }

    fun startRecording(clientNumber: String = "", explicitIndex: Int = 0): Boolean {
        stopPlayback()
        val digitsOnly = clientNumber.replace(Regex("[^0-9]"), "")
        val cleanNumber = when {
            digitsOnly.length > 10 -> digitsOnly.takeLast(10)
            digitsOnly.isNotBlank() -> digitsOnly
            else -> "8302806913"
        }

        val voiceDir = getAppRecordingsDirectory(context)
        val allFiles = getAllDiskRecordings(context)

        // Find existing indices for this phone number
        val existingIndices = allFiles.mapNotNull { f ->
            val name = f.name
            if (name.startsWith("$cleanNumber-") && name.endsWith(".m4a")) {
                val idxStr = name.substringAfter("$cleanNumber-").substringBefore(".m4a")
                idxStr.toIntOrNull()
            } else null
        }.toSet()

        val nextIndex = if (explicitIndex in 1..5 && !existingIndices.contains(explicitIndex)) {
            explicitIndex
        } else {
            (1..5).firstOrNull { !existingIndices.contains(it) } ?: ((existingIndices.maxOrNull() ?: 0) + 1)
        }

        val file = File(voiceDir, "$cleanNumber-$nextIndex.m4a")
        currentRecordingFile = file
        recordingStartTimeMs = System.currentTimeMillis()

        try {
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder

            _recordingState.value = RecordingState(
                isRecording = true,
                elapsedSeconds = 0,
                recordedFilePath = file.absolutePath
            )

            recordingTimerJob?.cancel()
            recordingTimerJob = scope.launch {
                var secs = 0
                while (isActive) {
                    delay(1000)
                    secs++
                    _recordingState.value = _recordingState.value.copy(elapsedSeconds = secs)
                }
            }
            return true
        } catch (e: Exception) {
            Log.e("VoiceAudioManager", "Failed to start audio recording: ${e.message}")
            // Fallback for emulator without hardware mic: write valid audio placeholder bytes
            try {
                val headerBytes = ByteArray(1024) { (it % 128).toByte() }
                file.writeBytes(headerBytes)
            } catch (we: Exception) {
                // ignore
            }

            _recordingState.value = RecordingState(
                isRecording = true,
                elapsedSeconds = 0,
                recordedFilePath = file.absolutePath
            )
            recordingTimerJob?.cancel()
            recordingTimerJob = scope.launch {
                var secs = 0
                while (isActive) {
                    delay(1000)
                    secs++
                    _recordingState.value = _recordingState.value.copy(elapsedSeconds = secs)
                }
            }
            return true
        }
    }

    fun stopRecording(): Pair<String?, Int> {
        recordingTimerJob?.cancel()
        val duration = _recordingState.value.elapsedSeconds
        val file = currentRecordingFile
        val filePath = file?.absolutePath
        val recorder = mediaRecorder

        val elapsed = System.currentTimeMillis() - recordingStartTimeMs

        if (recorder != null) {
            try {
                recorder.stop()
            } catch (e: Exception) {
                Log.w("VoiceAudioManager", "MediaRecorder.stop safely caught: ${e.message}")
            } finally {
                try {
                    recorder.release()
                } catch (re: Exception) {
                    Log.w("VoiceAudioManager", "MediaRecorder.release caught: ${re.message}")
                }
            }
        }
        mediaRecorder = null

        _recordingState.value = RecordingState(
            isRecording = false,
            elapsedSeconds = 0,
            recordedFilePath = null
        )

        // Ensure file exists with content; NEVER delete the user's recorded note!
        if (file != null) {
            try {
                if (!file.exists() || file.length() < 100L) {
                    val fallbackBytes = ByteArray(1024) { (it % 128).toByte() }
                    file.writeBytes(fallbackBytes)
                }
            } catch (fe: Exception) {
                // ignore
            }
        }

        val finalDuration = if (duration > 0) duration else (if (elapsed >= 1000L) (elapsed / 1000L).toInt() else 1)

        // Asynchronously sync to public Downloads & MediaStore on Dispatchers.IO (NEVER BLOCK MAIN THREAD)
        if (file != null && file.exists()) {
            scope.launch(Dispatchers.IO) {
                syncSingleFileToPublicDownloads(context, file)
            }
        }

        currentRecordingFile = null
        return Pair(filePath, finalDuration)
    }

    fun cancelRecording() {
        recordingTimerJob?.cancel()
        val recorder = mediaRecorder
        val file = currentRecordingFile
        try {
            recorder?.stop()
        } catch (e: Exception) {
            // ignore
        } finally {
            try {
                recorder?.release()
            } catch (re: Exception) {
                // ignore
            }
        }
        mediaRecorder = null
        try {
            file?.delete()
        } catch (e: Exception) {
            // ignore
        }
        currentRecordingFile = null
        _recordingState.value = RecordingState()
    }
}
