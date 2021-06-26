package net.kunmc.lab.worldslice;

import net.querz.mca.Chunk;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import net.querz.mca.Section;
import net.querz.nbt.tag.CompoundTag;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class WorldSliceConverter {
    public static void main(String... args) {
        int Y = 64;
        if (args.length > 0)
            try {
                Y = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Usage: java -jar worldsplit.jar <height>: " + args[0] + " is not a number.");
                System.exit(1);
            }

        System.out.println("Height: " + Y);

        File mcaDirectory = new File(".");
        File[] allMca = Objects.requireNonNull(mcaDirectory.listFiles(e -> e.getName().endsWith(".mca")));

        if (allMca.length <= 0) {
            System.err.println("No .mca files found.");
            System.exit(1);
        }

        System.out.println(String.format("Conversion started! %d files", allMca.length));

        AtomicInteger doneMcaCount = new AtomicInteger();
        int finalY = Y;
        CompletableFuture.allOf(
                Arrays.stream(allMca)
                        .map(e -> CompletableFuture.runAsync(() -> {
                            try {
                                convertMca(e, finalY);
                            } catch (IOException ex) {
                                throw new RuntimeException("Convert Error", ex);
                            }
                        }))
                        .map(e -> e.thenRun(() -> {
                            System.out.println(String.format("Done %d/%d", doneMcaCount.incrementAndGet(), allMca.length));
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();

        System.out.println("Finished!");
    }

    public static void convertMca(File mcaFile, int Y) throws IOException {
        if (mcaFile.length() == 0)
            return;

        CompoundTag tag = new CompoundTag();

        MCAFile mca = MCAUtil.read(mcaFile);
        IntStream.range(0, 1024).forEach(i -> {
            Chunk chunk = mca.getChunk(i);

            if (chunk == null)
                return;

            IntStream.range(0, 16)
                    .forEach(y -> {
                        Section section = chunk.getSection(y);
                        if (section == null)
                            return;
                        if (section.getPalette() == null)
                            return;
                        if (y != Y / 16) {
                            chunk.setSection(y, null);
                            return;
                        }

                        for (int iy = 0; iy < 16; ++iy)
                            if (iy != Y % 16)
                                for (int iz = 0; iz < 16; ++iz)
                                    for (int ix = 0; ix < 16; ++ix)
                                        section.setBlockStateAt(ix, iy, iz, tag, false);
                    });

            mca.setChunk(i, chunk);
        });
        MCAUtil.write(mca, mcaFile);
    }
}
