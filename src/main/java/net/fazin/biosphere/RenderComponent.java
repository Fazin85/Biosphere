package net.fazin.biosphere;

public class RenderComponent extends Component {
    private IRenderable renderable;

    public boolean hasRenderable() {
        return renderable != null;
    }

    public IRenderable getRenderable() {
        return renderable;
    }

    public void setRenderable(IRenderable renderable) {
        this.renderable = renderable;
    }
}
