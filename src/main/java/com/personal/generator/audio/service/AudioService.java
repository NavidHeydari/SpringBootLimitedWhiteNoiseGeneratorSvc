package com.personal.generator.audio.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AudioService {

	public void audioFileHandler(String output) throws IOException {

		File fileOut = new File(output);
		AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;// fileFormat.getType();

		AudioInputStream audioInputStream = null;

		audioInputStream = generateByteArrayIntoStream();

		if (AudioSystem.isFileTypeSupported(fileType, audioInputStream)) {
			AudioSystem.write(audioInputStream, fileType, fileOut);

		}
	}


	public AudioInputStream generateByteArrayIntoStream() throws IOException {
		int sampleRate = 44100; // 64000;
		int bitDepth = 16; // 16 bits
		int numberOfChannels = 2; // sterio
		int lookAheadMiliSec = 1500;
		
		int bitRate = sampleRate * bitDepth * numberOfChannels;
		
		int bytePerSec = bitRate/8;
		int lookAheadMiliSecokAheadInBytes = (bytePerSec/1000) * lookAheadMiliSec;
		

		byte[] byteBuffer = new byte[sampleRate];

		ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
		Random rand = new Random();

		// will take 2 sec
		for (int i = 0; i < byteBuffer.length / 2; i++) {
			byteBuffer[2 * i + 0] = (byte) rand.nextGaussian(1, 2);
			byteBuffer[2 * i + 1] = (byte) rand.nextGaussian(1,2);
		}

		//sampleRate,samplesizeinBites, channels,signed,littleIndian)
//		AudioInputStream stream = new AudioInputStream(bais, new AudioFormat(16000, 8, 2, true, false), sampleRate);

		AudioInputStream stream = new AudioInputStream(bais, new AudioFormat(sampleRate, bitDepth, numberOfChannels, true, false), sampleRate);
		
		return stream;
	}

	public String expandAnExistingFileIntoLargerFile(String filename) throws IOException {
		String expandedFileName = "expanded_" + filename;
		FileOutputStream fo = new FileOutputStream(expandedFileName);

		try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
			// move to the beginning of the file.
			raf.seek(0);

			byte[] buffer = new byte[1024*16];

			for (int i = 0; i < 1_000_000; i++) {

				while (raf.read(buffer) != -1) {
					fo.write(buffer);

				}
				fo.flush();
				raf.seek(0);
			}
			fo.close();
		}
		return expandedFileName;
	}

	
//******************************************************************** another way to develope the white noise.
	/**
	 * 
	 * @param fileName
	 * @param durationOfFileInHour
	 */
	public void whiteNoiseGeneratorInSterioForVariableDurationInHour(String fileName, int durationOfFileInHour) {
		
		try {
            // Set the duration and sample rate
            double durationInSeconds = durationOfFileInHour * 60 * 60; // 10 hours
            float sampleRate = 44100.0f; // CD-quality sample rate
            int numberOfChannels = 2 ; // sterio
            int bitRate = 16;

            // Calculate the number of frames
            int numFrames = (int) (durationInSeconds * sampleRate);

            // Create an audio format
            AudioFormat audioFormat = new AudioFormat(sampleRate, bitRate, numberOfChannels, true, true);

            // Create an audio file writer
            AudioInputStream audioInputStream = new AudioInputStream(
                    new ByteArrayInputStream(generateStereoWhiteNoise(numFrames)),
                    audioFormat, numFrames);

            // Specify the output file
            File outputFile = new File(fileName);

            // Write the audio data to the file
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private byte[] generateStereoWhiteNoise(int numFrames) {
        byte[] buffer = new byte[numFrames * 4]; // 16-bit PCM, 2 channels (stereo)

        for (int i = 0; i < numFrames; i++) {
            // Generate random samples for the left and right channels
            short leftSample = (short) (Math.random() * 65535 - 32767);
            short rightSample = (short) (Math.random() * 65535 - 32767);

            // Store the left channel sample in the buffer (little-endian)
            buffer[i * 4] = (byte) (leftSample & 0xFF);
            buffer[i * 4 + 1] = (byte) ((leftSample >> 8) & 0xFF);

            // Store the right channel sample in the buffer (little-endian)
            buffer[i * 4 + 2] = (byte) (rightSample & 0xFF);
            buffer[i * 4 + 3] = (byte) ((rightSample >> 8) & 0xFF);
        }

        return buffer;
    }
	
	
}
