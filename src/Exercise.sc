theme: /Exercise
    
    state: Start
        q!: $regex</practice>   
        if: $client.QuizQuestinNumber  
            a: {{contents['quiz_continue']}}
        else: 
            a: {{contents['quiz_start']}}
        timeout: /Exercise/Question || interval = "3 seconds"
        
    state: Question
        script: 
            $client.QuizQuestinNumber = $client.QuizQuestinNumber+=1 || 1;
            if ($client.QuizQuestinNumber > 10) {
                $client.QuizQuestinNumber = 11;
                $reactions.answer(contents.last_question_occured);
            }
            $reactions.answer(contents["quiz" + $client.QuizQuestinNumber]);
            sendInlineButtons($context, contents["options" + $client.QuizQuestinNumber]);
        
    state: Answer
        q: * || fromState = "/Exercise/Question", onlyThisState = true
        script: 
            var corrAnswer = contents["correct" + $client.QuizQuestinNumber];
            var DistortionName =  contents["distortion" + $client.QuizQuestinNumber];
            if ($request.query == corrAnswer) {
                var explanation = _.sample(contents.quiz_correct, 1);
                explanation += contents["explanation_correct" + $client.QuizQuestinNumber];
                log(explanation)
            }
            else {
                var explanation = _.sample(contents.quiz_incorrect, 1);
                explanation += contents["explanation_incorrect" + $client.QuizQuestinNumber];
                log(explanation)
            }
            $reactions.answer(explanation)
        timeout: /Exercise/Question || interval = "5 seconds"
