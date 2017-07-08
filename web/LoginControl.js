/**
 * Created by Ben Rosencveig on 06/10/2016.
 */

window.onload = function()
{
    checkLoginStatus();
    setInterval(checkLoginStatus, 2000);
};

function checkLoginStatus() {
    $.ajax
    ({
        url: 'login',
        data: {
            action: "status"
        },
        type: 'GET',
        success: statusCallback
    });
}

function statusCallback(json)
{
    if (json.isConnected && json.gameNumber != -1)
    {
        window.location = "GameRoom.html";
    }
    else if (json.isConnected)
    {
        window.location = "LobbyPage.html";
    }
}

function loginClick()
{
    event.preventDefault();

    var userName = $('.UserNameInput').val();
    var computerFlag = $('.ComputerCheckBox').is(':checked');

    //$.get('login', loginCallback);
    $.ajax
    ({
        url: 'login',
        data:
        {
            action: "login",
            userName: userName,
            isComputer: computerFlag
        },
        type: 'GET',
        success: loginCallback
    });
}

function loginCallback(json)
{
    if (json.isConnected)
    {
        window.location = "LobbyPage.html";
    }
    else
    {
        alert(json.errorMessage);
    }
}