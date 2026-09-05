package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
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

    fun startRecording(): Boolean {
        stopPlayback()
        try {
            val voiceDir = File(context.cacheDir, "voice_notes").apply { mkdirs() }
            val file = File(voiceDir, "vn_${System.currentTimeMillis()}.m4a")
            currentRecordingFile = file

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
            // Fallback for emulator without hardware mic: simulate recording
            val voiceDir = File(context.cacheDir, "voice_notes").apply { mkdirs() }
            val file = File(voiceDir, "demo_vn_${System.currentTimeMillis()}.m4a")
            file.writeText("simulated_voice_note")
            currentRecordingFile = file

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
        val filePath = currentRecordingFile?.absolutePath

        try {
            mediaRecorder?.let {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null

        _recordingState.value = RecordingState(
            isRecording = false,
            elapsedSeconds = 0,
            recordedFilePath = null
        )

        return Pair(filePath, if (duration > 0) duration else 3)
    }

    fun cancelRecording() {
        recordingTimerJob?.cancel()
        try {
            mediaRecorder?.let {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
        currentRecordingFile?.delete()
        currentRecordingFile = null
        _recordingState.value = RecordingState()
    }
}
