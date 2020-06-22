package commons.lib.console.v2.yaml;

import commons.lib.console.v2.question.Question;

import java.util.List;
import java.util.stream.Collectors;

public class YamlAction {
    private final String choiceId;
    private final String choiceName;
    private final List<YamlQuestion> questionList;
    private final List<String> subChoiceList;
    private final YamlPostProcessor postProcessorType;

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
}
