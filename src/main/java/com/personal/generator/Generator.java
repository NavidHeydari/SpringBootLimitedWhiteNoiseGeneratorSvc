package com.personal.generator;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.generator.audio.service.AudioService;

@RestController
public class Generator {

	@Autowired
	AudioService as;

	@GetMapping("generateHours")
	public String generateWhiteNoise() {

		String filename = "GausianWhiteNoise_" + UUID.randomUUID().toString() + ".wav";
		try {
			
			as.whiteNoiseGeneratorInSterioForVariableDurationInHour(filename, 3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return filename + " is created.";

	}
	
	
	@GetMapping("generate_1sec")
	public String generate() {

		String filename = "GausianWhiteNoise_" + UUID.randomUUID().toString() + ".wav";
		try {
			
			as.audioFileHandler(filename);
			filename = as.expandAnExistingFileIntoLargerFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return filename + " is created.";

	}

	@GetMapping("expand")
	public String expandFile() {
		String filename = "sample.mp3";
		try {
			
			filename = as.expandAnExistingFileIntoLargerFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return filename + " is created.";

	}
}
