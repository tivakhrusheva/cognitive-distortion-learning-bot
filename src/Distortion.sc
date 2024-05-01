require: functions.js
require: ./data/contents.yaml
    var = contents
require: ./data/distortions.js

theme: /Distortion
    
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
            # q: Distortion_ready || fromState = "/Distortion/DistortionBegin"
            event: telegramCallbackQuery || fromState = "/Distortion/DistortionBegin", onlyThisState = false
            # q: Дальше || fromState = "/Distortion/DistortionCard", onlyThisState = true
            # q: Distortion_next || fromState = "/Distortion/DistortionCard", onlyThisState = true
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
                    