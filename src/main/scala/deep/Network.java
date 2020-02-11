package deep;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.schedule.MapSchedule;
import org.nd4j.linalg.schedule.ScheduleType;

public class Network {
    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .l2(0.0005) // ridge regression value
            .updater(new Nesterovs(new MapSchedule(ScheduleType.ITERATION, learningRateSchedule)))
            .weightInit(WeightInit.XAVIER)
            .list()
            .layer(new ConvolutionLayer.Builder(5, 5)
                    .nIn(channels)
                    .stride(1, 1)
                    .nOut(20)
                    .activation(Activation.IDENTITY)
                    .build())
            .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                    .kernelSize(2, 2)
                    .stride(2, 2)
                    .build())
            .layer(new ConvolutionLayer.Builder(5, 5)
                    .stride(1, 1) // nIn need not specified in later layers
                    .nOut(50)
                    .activation(Activation.IDENTITY)
                    .build())
            .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                    .kernelSize(2, 2)
                    .stride(2, 2)
                    .build())
            .layer(new DenseLayer.Builder().activation(Activation.RELU)
                    .nOut(500)
                    .build())
            .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(outputNum)
                    .activation(Activation.SOFTMAX)
                    .build())
            .setInputType(InputType.convolutionalFlat(height, width, channels)) // InputType.convolutional for normal image
            .build();

    MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
}
