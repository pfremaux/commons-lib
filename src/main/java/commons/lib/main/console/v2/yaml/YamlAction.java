package commons.lib.main.console.v2.yaml;

import commons.lib.main.console.v2.question.Question;

import java.util.List;
import java.util.stream.Collectors;

public class YamlAction {
    private String choiceId;
    private String choiceName;
    private List<YamlQuestion> questionList;
    private List<String> subChoiceList;
    private YamlPostProcessor postProcessorType;

    public YamlAction() {
    }

    public YamlAction(String choiceId, String choiceName, List<YamlQuestion> questionList, List<String> subChoiceList, YamlPostProcessor postProcessorType) {
        this.choiceId = choiceId;
        this.choiceName = choiceName;
        this.questionList = questionList;
        this.subChoiceList = subChoiceList;
        this.postProcessorType = postProcessorType;
    }

    public String getChoiceId() {
        return choiceId;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public List<YamlQuestion> getQuestionList() {
        return questionList;
    }

    public List<Question> toQuestions() {
        return getQuestionList().stream().map(q -> new Question(q.getQuestion())).collect(Collectors.toList());
    }

    public List<String> getSubChoiceList() {
        return subChoiceList;
    }

    public YamlPostProcessor getPostProcessorType() {
        return postProcessorType;
    }

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public void setQuestionList(List<YamlQuestion> questionList) {
        this.questionList = questionList;
    }

    public void setSubChoiceList(List<String> subChoiceList) {
        this.subChoiceList = subChoiceList;
    }

    public void setPostProcessorType(YamlPostProcessor postProcessorType) {
        this.postProcessorType = postProcessorType;
    }
}
