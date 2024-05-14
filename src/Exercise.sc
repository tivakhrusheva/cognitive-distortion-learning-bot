require: ./data/practice_contents.yaml
    var = exercise_contents

theme: /Exercise
    
    state: Start
        q!: $regex</practice>   
        q!: $regex</practie>  
        q!: practice
        q!: practie
        q!: практика
        if: $client.QuizQuestinNumber > 1
            a: {{exercise_contents['quiz_continue']}}
            timeout: /Exercise/Question || interval = "2 seconds"
        else: 
            a: {{exercise_contents['quiz_start']}}
            inlineButtons:
                { text: "Далее", callback_data: "" }
            
    state: NextButtonProcessor 
        event: telegramCallbackQuery || fromState = "/Exercise/Start"
        event: telegramCallbackQuery || fromState = "/Exercise/Answer"
        if: $context.session.lastState == "/Exercise/Answer"
            go!: /Exercise/Question
        
    state: Question
        script: 
            $client.QuizQuestinNumber = $client.QuizQuestinNumber+=1 || 1;
            # $client.QuizQuestinNumber = $client.QuizQuestinNumber= 3;
            if ($client.QuizQuestinNumber > 10) {
                $client.QuizQuestinNumber = 11;
                $reactions.answer(exercise_contents.last_question_occured);
            }
            else {
                $reactions.answer(exercise_contents["quiz" + $client.QuizQuestinNumber]);
                log(exercise_contents["options" + $client.QuizQuestinNumber]);
                sendInlineButtons($context, exercise_contents["options" + $client.QuizQuestinNumber]);
            }
        
    state: Answer
        q: * || fromState = "/Exercise/Question", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Exercise/Question"
        script: 
            var corrAnswer = exercise_contents["correct" + $client.QuizQuestinNumber];
            log("corrAnswer")
            log(corrAnswer)
            var DistortionName = exercise_contents["distortion" + $client.QuizQuestinNumber];
            if ($request.query == corrAnswer) {
                var explanation = _.sample(exercise_contents.quiz_correct, 1);
                log("corr")
                log(exercise_contents["explanation_correct" + $client.QuizQuestinNumber]);
                explanation += "\n\n" + exercise_contents["explanation_correct" + $client.QuizQuestinNumber];
                log(explanation)
            }
            else {
                var explanation = _.sample(exercise_contents.quiz_incorrect, 1);
                log(exercise_contents["explanation_correct" + $client.QuizQuestinNumber]);
                explanation += "\n\n" + exercise_contents["explanation_correct" + $client.QuizQuestinNumber];
                log(explanation)
            }
            $reactions.answer(explanation)
            if ($client.QuizQuestinNumber < 10) {
                $reactions.inlineButtons({ text: "Далее", callback_data: "Next_situation" });
            }
        # timeout: /Exercise/Question || interval = "5 seconds"
