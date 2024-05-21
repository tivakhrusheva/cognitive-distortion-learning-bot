require: ./data/feedback_contents.yaml
    var = feedback_contents

theme: /Feedback


    state: UserInput
        q!: $regex</feedback>
        a: {{feedback_contents.request_feedback}}
        
        state: MerciUser
            q: * || fromState = "/Feedback/UserInput", onlyThisState = true
            a: {{feedback_contents.feedback_merci}}
            go: /Start