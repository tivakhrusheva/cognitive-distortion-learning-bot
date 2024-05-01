require: functions.js
require: ./data/contents.yaml
    var = contents
require: ./data/distortions.js

theme: /Distortion
    
    state: CallBackProcessor
        event: telegramCallbackQuery || fromState = "/Distortion/DistortionBegin", onlyThisState = false
        if: $request.query == "/train"
            go!: /Exercise/Start
        elseif: $request.query == "Distortion_ready"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Distortion_next"
            go!: /Distortion/DistortionBegin/DistortionCard
        elseif: $request.query == "Distortion_back_to_menu"
            go!: /Start
    
    state: DistortionBegin
        q!: $regex</learn>
        if: $client.cardNumber
            go!: DistortionCard
        else:
            a: {{contents.distortion_begin}}
            inlineButtons:
                {text: "Да", callback_data: "Distortion_ready"}
                {text: "Нет", callback_data: "Distortion_not_ready"}
            
        state: DistortionCard
            q: Да || fromState = "/Distortion/DistortionBegin"
            script:
                $client.cardNumber = $client.cardNumber+=1 || 0;
                sendCard($context, urls, $client.cardNumber);
                if ($client.cardNumber < Object.keys(urls).length) {
                    $response.replies = $response.replies || [];
                    $response.replies.push(
                    {
                      "type": "inlineButtons",
                      "buttons": [
                        {
                          "text": "Дальше",
                          "callback_data": "Distortion_next"
                        }
                      ]
                    });
                }
                else {
                    $response.replies = $response.replies || [];
                    $response.replies.push(
                    {
                      "type": "inlineButtons",
                      "buttons": [
                        {
                          "text": "Тренироваться",
                          "callback_data": "/train"
                        }
                      ]
                    });
                }
            inlineButtons:
                {text: "Назад в меню", callback_data: "Distortion_back_to_menu"}
                    