package kia.shamaei.serverapp.service.pretrainModel;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.io.*;

@Service
@Log4j2
public class PretrainModelServiceImpl implements PretrainModelService {
    private SavedModelBundle model;

    @Override
    public String recogniseImage(byte[] nameOfFile , String nameOfModel) {
        Tensor<Float> input = reshape(nameOfFile);
        model = SavedModelBundle.load("resnet.h5", "serve");
        Tensor<Float> output = model
                .session()
                .runner()
                .feed("input", input)
                .fetch("output")
                .run()
                .get(0)
                .expect(Float.class);

        log.info("this is out put {} " , output.toString() );
        return "rrr";
    }


    @Override
    public String savedTempImage(byte[] image) {
            String tempFileName = "image.jpg";
            // Save the image to a temporary file
            File tempFile = new File(tempFileName);
            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(image);
            }catch (Exception e){
                e.printStackTrace();
            }
            return tempFileName;
    }
    public static Tensor<Float> reshape (byte[] nameOfFile){
        // Reshape the byte array to a 4D tensor
        Tensor<Float> input = Tensor.create(new long[]{1, 224, 224, 3}, Float.class);
        float[] pixels = new float[224 * 224 * 3];
        for (int i = 0; i < nameOfFile.length; i += 3) {
            int r = nameOfFile[i] & 0xFF;
            int g = nameOfFile[i + 1] & 0xFF;
            int b = nameOfFile[i + 2] & 0xFF;
            pixels[i / 3] = (float) r / 255.0f;
            pixels[i / 3 + 1] = (float) g / 255.0f;
            pixels[i / 3 + 2] = (float) b / 255.0f;
        }
        input.copyTo(pixels);
        return input;
    }
    public static int argmax(byte[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxIndex = i;
                maxValue = array[i];
            }
        }
        return maxIndex;
    }
}
