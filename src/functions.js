function sendCard(context, object, i) {
    log("obj " + object)
    log("index " + i)
    context.response.replies = context.response.replies || [];
    context.response.replies.push({
        "type": "image",
        "imageUrl": object[Object.keys(object)[i]],
        "imageName": "<u>" + Object.keys(object)[i] + "</u>",
        "markup": "html"
    });
}

