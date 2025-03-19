package net.fazin.biosphere.engine.component;

import net.fazin.biosphere.engine.graphics.IRenderable;

public class RenderComponent extends Component {
    private IRenderable renderable;

    public RenderComponent(IRenderable renderable) {
        this.renderable = renderable;
    }

    public boolean hasRenderable() {
        return renderable != null;
    }

    public IRenderable getRenderable() {
        return renderable;
    }

    public void setRenderable(IRenderable renderable) {
        this.renderable = renderable;
    }

    @Override
    public void destroyed() {
        if (renderable != null) {
            renderable.destroy();
        }
    }
}
