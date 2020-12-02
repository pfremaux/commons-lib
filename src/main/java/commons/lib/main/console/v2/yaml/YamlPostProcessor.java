package commons.lib.main.console.v2.yaml;

import commons.lib.main.console.v2.action.PostProcessorType;

public class YamlPostProcessor {

    private String info;
    private PostProcessorType yamlPostProcessor;

    public YamlPostProcessor() {
    }

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

    public void setInfo(String info) {
        this.info = info;
    }

    public void setYamlPostProcessor(PostProcessorType yamlPostProcessor) {
        this.yamlPostProcessor = yamlPostProcessor;
    }
}
