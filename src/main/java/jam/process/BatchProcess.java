
package jam.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.util.CollectionUtil;

public abstract class BatchProcess<T,R> {
    private final int batchSize;

    private final Collection<T> inputFull;
    private final List<List<T>> inputBatch;

    private final List<R> outputFull;
    private final List<List<R>> outputBatch;

    /**
     * Creates a new batch process for a given collection of inputs
     * and a fixed batch size.
     *
     * @param inputFull all input objects to process.
     *
     * @param batchSize the maximum size of each batch.
     */
    protected BatchProcess(Collection<T> inputFull, int batchSize) {
        this.batchSize = batchSize;

        this.inputFull = inputFull;
        this.inputBatch = CollectionUtil.split(inputFull, batchSize);

        this.outputFull = new ArrayList<R>(inputFull.size());
        this.outputBatch = new ArrayList<List<R>>(batchSize);
    }

    /**
     * Runs a single batch of input objects.
     *
     * @param inputSlice the current slice input objects for the batch.
     *
     * @return the result of the process for each object in the input
     * slice.
     */
    public abstract List<R> runBatch(Collection<T> inputSlice);

    /**
     * Runs the batches sequentially and collects the output into a
     * single list.
     *
     * @return the full list of output objects.
     */
    public List<R> runSequential() {
        for (List<T> batchList : inputBatch)
            outputBatch.add(runBatch(batchList));

        collectOutput();
        return outputFull;
    }

    private void collectOutput() {
        for (List<R> batchList : outputBatch)
            outputFull.addAll(batchList);
    }
}
