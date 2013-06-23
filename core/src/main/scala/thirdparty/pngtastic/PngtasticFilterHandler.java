package thirdparty.pngtastic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Implement PNG filtering and defiltering
 *
 * @author rayvanderborght
 */
public class PngtasticFilterHandler implements PngFilterHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyFiltering(PngFilterType filterType, List<byte[]> scanlines, int sampleBitCount) {
        int scanlineLength = scanlines.get(0).length;
        byte[] previousRow = new byte[scanlineLength];
        for (byte[] scanline : scanlines) {
            if (filterType != null) {
                scanline[0] = filterType.getValue();
            }

            byte[] previous = scanline.clone();

            try {
                this.filter(scanline, previousRow, sampleBitCount);
            } catch (PngException ignored) {
            }
            previousRow = previous;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyAdaptiveFiltering(byte[] inflatedImageData, List<byte[]> scanlines, Map<PngFilterType, List<byte[]>> filteredScanLines, int sampleSize) throws IOException {
        for (int s = 0; s < scanlines.size(); s++) {
            long bestSum = Long.MAX_VALUE;
            PngFilterType bestFilterType = null;
            for (Map.Entry<PngFilterType, List<byte[]>> entry : filteredScanLines.entrySet()) {
                long sum = 0;
                byte[] scanline = entry.getValue().get(s);
                for (int i = 1; i < scanline.length; i++) {
                    sum += Math.abs(scanline[i]);
                }

                if (sum < bestSum) {
                    bestFilterType = entry.getKey();
                    bestSum = sum;
                }
            }
            if (bestFilterType != null) {
                scanlines.get(s)[0] = bestFilterType.getValue();
            }
        }

        this.applyFiltering(null, scanlines, sampleSize);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The bytes are named as follows (x = current, a = previous, b = above, c = previous and above)
     * <pre>
     * c b
     * a x
     * </pre>
     */
    @Override
    public void filter(byte[] line, byte[] previousLine, int sampleBitCount) throws PngException {
        PngFilterType filterType = PngFilterType.forValue(line[0]);
        line[0] = 0;

        PngFilterType previousFilterType = PngFilterType.forValue(previousLine[0]);
        previousLine[0] = 0;

        switch (filterType) {
            case NONE:
                break;

            case SUB: {
                byte[] original = line.clone();
                int previous = -(Math.max(1, sampleBitCount / 8) - 1);
                for (int x = 1, a = previous; x < line.length; x++, a++) {
                    line[x] = (byte) (original[x] - ((a < 0) ? 0 : original[a]));
                }
                break;
            }
            case UP: {
                for (int x = 1; x < line.length; x++) {
                    line[x] = (byte) (line[x] - previousLine[x]);
                }
                break;
            }
            case AVERAGE: {
                byte[] original = line.clone();
                int previous = -(Math.max(1, sampleBitCount / 8) - 1);
                for (int x = 1, a = previous; x < line.length; x++, a++) {
                    line[x] = (byte) (original[x] - ((0xFF & original[(a < 0) ? 0 : a]) + (0xFF & previousLine[x])) / 2);
                }
                break;
            }
            case PAETH: {
                byte[] original = line.clone();
                int previous = -(Math.max(1, sampleBitCount / 8) - 1);
                for (int x = 1, a = previous; x < line.length; x++, a++) {
                    int result = this.paethPredictor(original, previousLine, x, a);
                    line[x] = (byte) (original[x] - result);
                }
                break;
            }
            default:
                throw new PngException("Unrecognized filter type " + filterType);
        }
        line[0] = filterType.getValue();
        previousLine[0] = previousFilterType.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deFilter(byte[] line, byte[] previousLine, int sampleBitCount) throws PngException {
        PngFilterType filterType = PngFilterType.forValue(line[0]);
        line[0] = 0;

        PngFilterType previousFilterType = PngFilterType.forValue(previousLine[0]);
        previousLine[0] = 0;

        switch (filterType) {
            case SUB: {
                int previous = -(Math.max(1, sampleBitCount / 8) - 1);
                for (int x = 1, a = previous; x < line.length; x++, a++) {
                    line[x] = (byte) (line[x] + ((a < 0) ? 0 : line[a]));
                }
                break;
            }
            case UP: {
                for (int x = 1; x < line.length; x++) {
                    line[x] = (byte) (line[x] + previousLine[x]);
                }
                break;
            }
            case AVERAGE: {
                int previous = -(Math.max(1, sampleBitCount / 8) - 1);
                for (int x = 1, a = previous; x < line.length; x++, a++) {
                    line[x] = (byte) (line[x] + ((0xFF & ((a < 0) ? 0 : line[a])) + (0xFF & previousLine[x])) / 2);
                }
                break;
            }
            case PAETH: {
                int previous = -(Math.max(1, sampleBitCount / 8) - 1);
                for (int x = 1, xp = previous; x < line.length; x++, xp++) {
                    int result = this.paethPredictor(line, previousLine, x, xp);
                    line[x] = (byte) (line[x] + result);
                }
                break;
            }
        }
        line[0] = filterType.getValue();
        previousLine[0] = previousFilterType.getValue();
    }

    /* */
    private int paethPredictor(byte[] line, byte[] previousLine, int x, int xp) {
        int a = 0xFF & ((xp < 0) ? 0 : line[xp]);
        int b = 0xFF & previousLine[x];
        int c = 0xFF & ((xp < 0) ? 0 : previousLine[xp]);
        int p = a + b - c;

        int pa = (p >= a) ? (p - a) : -(p - a);
        int pb = (p >= b) ? (p - b) : -(p - b);
        int pc = (p >= c) ? (p - c) : -(p - c);

        if (pa <= pb && pa <= pc) {
            return a;
        }

        return (pb <= pc) ? b : c;
    }
}
