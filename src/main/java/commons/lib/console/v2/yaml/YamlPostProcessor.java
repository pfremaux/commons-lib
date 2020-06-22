package commons.lib.console.v2.yaml;

public class YamlPostProcessor {

    private final String info;
    private final PostProcessorType yamlPostProcessor;

    public YamlPostProcessor(String info, PostProcessorType yamlPostProcessor) {
        this.info = info;
        this.yamlPostProcessor = yamlPostProcessor;
    }

    public String getInfo() {
        return info;
    }

    public PostProcessorType getYamlPostProcessor() {
        return yamlPostProcessor;
    }
}
