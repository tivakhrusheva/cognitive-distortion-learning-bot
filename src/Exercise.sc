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
                { text: "Хорошо", callback_data: "Begin_practice" }
                { text: "Вернуться в меню", callback_data: "To_menu" }
    
    state: Zero
        q!: (zero_ex|ex_zero)
        script:
            $client.QuizQuestinNumber = 0
        a: Обнулились..
            
    state: NextButtonProcessor 
        event: telegramCallbackQuery || fromState = "/Exercise/Start"
        event: telegramCallbackQuery || fromState = "/Exercise/Answer"
        script:
            log("$context.session.lastState")
            log($context.session.lastState)
        if: ($context.session.lastState == "/Exercise/Answer") && ($request.query == "Next_situation") 
            go!: /Exercise/Question
            
        elseif: ($context.session.lastState == "/Exercise/Start") && ($request.query == "To_menu") 
            go!: /Start/CommandDescription
            
        elseif: ($context.session.lastState == "/Exercise/Start") && ($request.query == "Begin_practice") 
            go!: /Exercise/Question
            
        elseif: ($context.session.lastState == "/Exercise/Answer") && ($request.query == "To_menu") 
            go!: /Start/CommandDescription
        
        elseif: ($context.session.lastState == "/Exercise/Question") && ($request.query == "To_menu") 
            go!: /Start/CommandDescription
            
        elseif: ($context.session.lastState == "/Exercise/Question") && ($request.query == "To_diary") 
            go!: /Diary/Start
            
    state: Question
        script: 
            # $client.QuizQuestinNumber = $client.QuizQuestinNumber= 1;
            if ($client.QuizQuestinNumber > 10) {
                $client.QuizQuestinNumber = 11;
                $reactions.answer(exercise_contents.last_question_occured);
                $reactions.inlineButtons({ text: "Дневник искажений", callback_data: "To_diary" });
                $reactions.inlineButtons({ text: "Вернуться в меню", callback_data: "To_menu" });
            }
            else {
                $reactions.answer(exercise_contents["quiz" + $client.QuizQuestinNumber]);
                log(exercise_contents["options" + $client.QuizQuestinNumber]);
                sendInlineButtonsWIndeces($context, exercise_contents["options" + $client.QuizQuestinNumber]);
            }
        
    state: Answer
        q: * || fromState = "/Exercise/Question", onlyThisState = true
        event: telegramCallbackQuery || fromState = "/Exercise/Question"
        script: 
            $client.QuizQuestinNumber = $client.QuizQuestinNumber+=1 || 1;
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
                $reactions.inlineButtons({ text: "Следующий вопрос", callback_data: "Next_situation" });
                $reactions.inlineButtons({ text: "Вернуться в меню", callback_data: "To_menu" });
            }
            
        # timeout: /Exercise/Question || interval = "5 seconds"
