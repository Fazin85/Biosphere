package net.fazin.biosphere;

import org.joml.Vector2i;

public class TextureAtlas {
    private final int size;
    private final TextureAtlasEntry[][] entries;
    private final Texture texture;

    public TextureAtlas(Texture texture, int size) {
        this.texture = texture;
        this.size = size;

        entries = new TextureAtlasEntry[size][size];
    }

    public void bind() {
        texture.bind();
    }

    public void unbind() {
        texture.unbind();
    }

    public void setEntry(TextureAtlasEntry entry) {
        entries[entry.x()][entry.y()] = entry;
    }

    public Vector2i indexOfEntry(String entryName) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (entries[x][y] == null) {
                    continue;
                }

                if (entries[x][y].name().equals(entryName)) {
                    return new Vector2i(x, y);
                }
            }
        }

        return null;
    }

    public TextureAtlasUVs getTextureCoordinates(int xIndex, int yIndex) {
        TextureAtlasEntry entry = entries[xIndex][yIndex];

        float tileSize = 1.0f / size;

        float uStart = entry.x() * tileSize;
        float vStart = entry.y() * tileSize;
        float uEnd = uStart + tileSize;
        float vEnd = vStart + tileSize;

        return new TextureAtlasUVs(uStart, vStart, uEnd, vEnd);
    }
}
