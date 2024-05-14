require: ./data/practice_contents.yaml
    var = exercise_contents

theme: /Exercise
    
    state: Start
        q!: $regex</practice>   
        q!: $regex</practie>  
        q!: practice
        q!: practie
        if: $client.QuizQuestinNumber  
            a: {{exercise_contents['quiz_continue']}}
        else: 
            a: {{exercise_contents['quiz_start']}}
        timeout: /Exercise/Question || interval = "2 seconds"
        
    state: Question
        script: 
            #$client.QuizQuestinNumber = $client.QuizQuestinNumber+=1 || 1;
            $client.QuizQuestinNumber = $client.QuizQuestinNumber= 1;
            if ($client.QuizQuestinNumber > 10) {
                $client.QuizQuestinNumber = 11;
                $reactions.answer(exercise_contents.last_question_occured);
            }
            $reactions.answer(exercise_contents["quiz" + $client.QuizQuestinNumber]);
            log(exercise_contents["options" + $client.QuizQuestinNumber]);
            sendInlineButtons($context, exercise_contents["options" + $client.QuizQuestinNumber]);
        
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
        timeout: /Exercise/Question || interval = "5 seconds"
