function sendCard(context, object, i, caption) {
    context.response.replies = context.response.replies || [];
    if (i < Object.keys(object).length) {
        if (!caption) {
            context.response.replies.push({
                "type": "image",
                "imageUrl": object[Object.keys(object)[i]],
                "imageName": "<b>" + Object.keys(object)[i] + "</b>",
                "markup": "html"
            })
        }
        else {
            context.response.replies.push({
                "type": "image",
                "imageUrl": object[Object.keys(object)[i]]
            })
        }}
    else {
            context.response.replies.push({
                "type": "text",
                "text": "Вы изучили все искажения, которые мне известны! Время переходить к практике.\n Введите команду /train.",
                "markup": "html"
            })
        }
};


function sendInlineButtons(context, buttonsNames) {
    context.response.replies = context.response.replies || [];
    var buttons = [];
    for (var i = 0; i < buttonsNames.length; i++) {
        buttons.push({
          "text": buttonsNames[i],
          "callback_data": buttonsNames[i],
    })
    };
    context.response.replies.push(
        {
        "type": "inlineButtons",
        "buttons": buttons
        }
    )
};


function sendInlineButtonsWIndeces(context, buttonsNames) {
    context.response.replies = context.response.replies || [];
    var buttons = [];
    for (var i = 0; i < buttonsNames.length; i++) {
        buttons.push({
          "text": buttonsNames[i],
          "callback_data": i,
    })
    };
    context.response.replies.push(
        {
        "type": "inlineButtons",
        "buttons": buttons
        }
    )
};


function sendMultipleCards(context, object1, object2, i) {
    context.response.replies = context.response.replies || [];
    if (i < Object.keys(object1).length) {
        context.response.replies.push({
          "type": "raw",
          "body": {
          "media": [
            {
            "type": "photo",
            "media": object1[Object.keys(object1)[i]],
            "parse_mode": "html"
            },
            {
            "type": "photo",
            "media": object2[Object.keys(object2)[i]],
            }
        ]
          },
          "method": "sendMediaGroup"
        }
        );
        $reactions.answer("<b>" + Object.keys(object1)[i] + "</b>");
        sendInlineButtons(context, ["Дальше", "В меню"]);
    }
    else {
            $reactions.transition("/Distortion/DistortionBegin/DistortionDistinction");
        // context.response.replies.push({
        //     "type": "text",
        //     "text": "Вы изучили все искажения, которые мне известны! Самое время переходить к практике — для этого введите команду /practice.",
        //     "markup": "html"
        // });

        // context.response.replies.push(
        //             {
        //               "type": "inlineButtons",
        //               "buttons": [
        //                 {
        //                   "text": "Практика",
        //                   "callback_data": "/train"
        //                 },
        //                 {
        //                   "text": "Назад в меню",
        //                   "callback_data": "Distortion_back_to_menu"
        //                 }
        //               ]
        //             });
    }
};

function predict(question) {
    var body = 
        {
        "clientId":"foobar",
        "input": SYSTEM_PROMPT + "Ситуация: " + question
    };
    var headers = {
        "Content-Type": "application/json",
        "MLP-API-KEY": $secrets.get("MLP_API_KEY")
    };
    log("body")
    log(body)

    var result = $http.post("https://caila.io/api/mlpgate/account/1000062767/model/51022/predict?configId=499", {
        body: body,
        dataType: "json",
        headers: headers
    });
    
    if (result.isOk && result.data.replies[0]) {
        return result.data.replies[0].text || null;
    }
    return null;
}

_.sample = _.wrap(_.sample, function(_sample, array, n) {
    return $jsapi.context().testContext ? (n ? array.slice(0, n) : array[0]) : _sample(array, n);
});


function showDiaryNote(situation, emotion, strenth_before, autothought, rat_response, strenth_after) {
    var Text = "<b>Ситуация</b>: " + situation +
     "\n<b>Эмоция</b>: " + emotion + 
     "\n<b>Сила эмоции</b>: " + strenth_before +
     "\n<b>Автомысль</b>: " + autothought +
     "\n<b>Рациональный ответ</b>: " + rat_response +
     "\n<b>Сила эмоции после рационального ответа</b>: " + strenth_after;
    $reactions.answer("<b>Ваша запись</b>:\n\n" + Text)
};


function prepareHistory(filtered_array) {
    var History = "";
    for (var i = 0; i < filtered_array.length; i++) {
      var Text = "<b>Ситуация</b>: " + filtered_array[i]["Thought"] +
       "\n<b>Эмоция</b>: " + filtered_array[i]["Emotion"]  + 
       "\n<b>Сила эмоции</b>: " + filtered_array[i]["Intensivity"] +
       "\n<b>Автомысль</b>: " + filtered_array[i]["AutoThought"] +
       "\n<b>Рациональный ответ</b>: " + filtered_array[i]["Rational"] +
       "\n<b>Сила эмоции после рационального ответа</b>: " + filtered_array[i]["IntensivityRepeat"];
       History+="\n" + Text + "\n";
  }
  return History;
  }
 
function filterByPeriod(client, filter_mode) {
    var today = new Date();
    today.setHours(today.getHours() + 3);
    log("today" + today)
    if (filter_mode == "hour") {
        log("hour")
        var periodEnd = new Date(new Date().getTime()- (22 * 60 * 60 * 1000))
        var periodStart = new Date(new Date().today.getTime() - (25 * 60 * 60 * 1000))
    }
    if (filter_mode == "day") {
        log("day")
        var periodStart = today.getTime() - (24 * 60 * 60 * 1000);
        var periodEnd = today;
    }
    if (filter_mode == "week") {
        log("week")
        var periodEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1);
        var periodStart = new Date(today.getFullYear(), today.getMonth(), today.getDate()-6);
    }
    if (filter_mode == "month") {
        log("month")
        var periodEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1);
        var periodStart = new Date(today.getFullYear(), today.getMonth(), today.getDate()-30);
    }
    if (filter_mode == "year") {
        log("year")
        var periodEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1);
        var periodStart = new Date(today.getFullYear(), today.getMonth(), today.getDate()-364);
    }
    
    log("Period Start was " + periodStart.toLocaleString());
    log("Now it is " + periodEnd.toLocaleString());
    var result = client.DiaryHistory.filter(function(d) {
        var time = new Date(d.Date)
        log("time: " + new Date(d.Date).toLocaleString());
        log("Period Start" + periodStart.toLocaleString());
        log("Period End" + periodEnd.toLocaleString());
        // var lastHourEnd = new Date(new Date().getTime()- (22 * 60 * 60 * 1000))
        // lastHourEnd.setHours(lastHourEnd.getHours() + 3);
        // var lastHourEnd = lastHourEnd.getTime()- (25 * 60 * 60 * 1000)
        log("Period End" + lastHourEnd.toLocaleString());
        log(time >= periodStart && time < periodEnd)
        return (time >= periodStart && time < periodEnd);
                });
    return result;
  }