/**
 * Created by user on 11/10/2016.
 */

var dictContent = null;
var xmlContent = null;
var xmlFile;
var readerXml = new FileReader();
var readerDict = new FileReader();

window.onload = function ()
{
    refreshLoginStatus();
    refreshUserList();
    setInterval(refreshUserList, 2000);
    setInterval(refreshGamesList, 2000);
    setInterval(refreshLoginStatus, 2000);
};

// window.onunload = function (event)
// {
//     if (didUserCloseWindow)
//     {
//         $.ajax
//         ({
//             async: false,
//             url: 'login',
//             data: {
//                 action: "close"
//             },
//             type: 'POST'
//         });
//     }
// }

function setdictFileFullName(event) {
    var dictFileFullName = event.target.files[0];
    readerDict = new FileReader();
    if (dictFileFullName != undefined && dictFileFullName.name.split('.').pop().toUpperCase() == "TXT")
    {
        readerDict.onload = function(e) {
            dictContent = readerDict.result;
        }
        readerDict.readAsText(dictFileFullName);
    } else {
        dictContent = null;
    }
}

function setXmlFileFullName(event) {
    xmlFile = event.target.files[0];
	readerXml = new FileReader();
    if (xmlFile != undefined && xmlFile.name.split('.').pop().toUpperCase() == "XML")
    {
        readerXml.onload = function(e) {
            xmlContent = readerXml.result;
        }
        readerXml.readAsText(xmlFile);
    } else {
        xmlContent = null;
    }
}

function checkIfuserInGame() {
    var result;
    $.ajax
    ({
        async: false,
        url: 'login',
        data: {
            action: "status"
        },
        type: 'GET',
        success: function (json) {
            result = json.gameNumber != -1;
        }
    });
    return result;
}

function getUserName() {
    var result;
    $.ajax
    ({
        async: false,
        url: 'login',
        data: {
            action: "status"
        },
        type: 'GET',
        success: function (json) {
            result = json.userName;
        }
    });
    return result;
}

function isUserComputer() {
    var result;
    $.ajax
    ({
        async: false,
        url: 'login',
        data: {
            action: "status"
        },
        type: 'GET',
        success: function (json) {
            result = json.isComputer;
        }
    });
    return result;
}

function refreshLoginStatus() {
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
    if (!json.isConnected)
    {
        window.location = "index.html";
    }
    else if (json.gameNumber != -1)
    {
        window.location = "GameRoom.html";
    }
    else
    {
        $('.userNameSpan').text("Hello " + json.userName + ", logged in as " + (json.isComputer ? "computer" : "human"));
    }
}

function onLogoutClick() {
    $.ajax(
        {
            url: 'login',
            data: {
                action: "logout"
            },
            type: 'GET',
            success: logoutCallback
        }
    );
}

function logoutCallback(json) {
    didUserCloseWindow = false;
    window.location = "/";
}

function refreshUserList() {
    $.ajax(
        {
            url: 'login',
            data: {
                action: "users"
            },
            type: 'GET',
            success: refreshUserListCallback
        }
    );
}

function refreshUserListCallback(json) {
    var usersTable = $('.usersTable tbody');
    usersTable.empty();
    var userList = json.users;

    userList.forEach(function (user) {

        var tr = $(document.createElement('tr'));

        var td = $(document.createElement('td')).text(user.name);

        td.appendTo(tr);

        tr.appendTo(usersTable);

    });
}


function loadGameClicked() {
    if (xmlContent != null && dictContent != null && document.getElementById("XmlFileInput").value != "" && document.getElementById("DictionaryFileInput").value != "")
    {
        $.ajax(
            {
                url: 'games',
                data: {
                    action: "loadGame",
                    xml: xmlContent,
                    dictionary: dictContent,
                    creatorName: getUserName()
                },
                type: 'POST',
                success: loadGameCallback
            }
        );

        $.ajax // Getting creator's name.
        ({
            url: 'login',
            data: {
                action: "status"
            },
            type: 'GET',
            success: function (json) {
                creatorName = getUserName();
                readerXml.readAsText(xmlFile);
            }
        });
    }

}

function loadGameCallback(json) {
    if (json.isLoaded) {
        alert("Load game Success !!");
        refreshGamesList();
        clearFileInput();
    }
    else {
        clearFileInput();
        alert(json.errorMessage);
    }
}

function refreshGamesList() {
    $.ajax
    (
        {
            url: 'games',
            data: {
                action: 'gameList'
            },
            type: 'GET',
            success: refreshGamesListCallback
        }
    )
}

function refreshGamesListCallback(json) {
    var gamesTable = $('.gamesTable tbody');
    gamesTable.empty();
    var gamesList = json.games;

    gamesList.forEach(function (game) {
        var tr = $(document.createElement('tr'));
        var tdGameNumber = $(document.createElement('td')).text(game.key);
        var tdGameName = $(document.createElement('td')).text(game.gameTitle);
        var tdCreatorName = $(document.createElement('td')).text(game.creatorName);
        var tdBoardSize = $(document.createElement('td')).text(game.rows + " X " + game.cols);
        var tdPlayerNumber = $(document.createElement('td')).text(game.registeredPlayers + " / " + game.requiredPlayers);
        var tdNumberOfChars = $(document.createElement('td')).text(game.numOfChars);
        var tdDictionaryName = $(document.createElement('td')).text(game.dictName);

        tdGameNumber.appendTo(tr);
        tdGameName.appendTo(tr);
        tdCreatorName.appendTo(tr);
        tdBoardSize.appendTo(tr);
        tdPlayerNumber.appendTo(tr);
            tdNumberOfChars.appendTo(tr);
        tdDictionaryName.appendTo(tr);

        tr.appendTo(gamesTable);
    });

    var tr = $('.tableBody tr');
    for (var i = 0; i < tr.length; i++) {
        tr[i].onclick = createGameDialog;
    }
}

function removeGameDialog() {
    $('.dialogDiv')[0].style.display = "none";
}

function clearFileInput() {
	document.getElementById("XmlFileInput").value = ""
    document.getElementById("DictionaryFileInput").value = "";
}

function createGameDialog(event) {
    var td = event.currentTarget.children[0];
    var number = td.innerText;
    $.ajax
    (
        {
            url: 'games',
            data: {
                action: 'gameDetails',
                key: number
            },
            type: 'GET',
            success: createGameDialogCallback
        }
    )
}

function createGameDialogCallback(json) {
    var div = $('.dialogDiv')[0];
    div.style.display = "block";
    var playersNamesDiv = $('.playersNames');

    var key = json.key;
    var creatorName = json.creatorName;
    var gameName = json.gameTitle;
    var boardSize = json.rows + " X " + json.cols;
    var playerNumber = json.registeredPlayers + " / " + json.requiredPlayers

    $('.key').text("Game id: " + key + ".");
    $('.creatorName').text("Game Creator: " + creatorName + ".");
    $('.gameName').text("Game Title: " + gameName);
    $('.boardSize').text("Board size: " + boardSize);
    $('.playerNumber').text("Players : " + playerNumber);
    for (i = 0; i < json.registeredPlayers; i++) {
        var playerDiv = $(document.createElement('div'));
        playerDiv.addClass('playerDiv');
        playerDiv.appendTo(playersNamesDiv);
    }

    var playerDivs = $('.playerDiv');
    for (i = 0; i < json.registeredPlayers; i++) {
        playerDivs[i].innerHTML = (+i + 1) + '. ' + json.players[i].m_Name + '.';
    }

    createBoard(json.rows, json.cols);
}

function createBoard(rows, cols) {
    var board = document.getElementById("mainBoardBody");
    board.innerHTML= "";
    for (i = 0; i < rows; i++) { // creates squares + row blocks.
        var row = board.insertRow(i);

        for (j = 0; j < cols; j++) { // add the squares.
            var cell = row.insertCell(j);
            cell.innerHTML = "?"
        }
    }
}

function joinGameClicked() {
    var name = getUserName();
    var isComputer = isUserComputer();
    var gameId = getGameId();
    $.ajax
    (
        {
            url: 'games',
            data: {
                action: 'joinGame',
                user: name,
                isComputer: isComputer,
                gameId: gameId
            },
            type: 'GET',
            success: joinGameClickedCallback
        }
    );
}

function joinGameClickedCallback(json) {

    if (json.isLoaded)
    {
        didUserCloseWindow = false;
        window.location = "GameRoom.html";
    }
    else {
        alert(json.errorMessage);
    }
}

function getGameId() {
    var string = $('.key').text();
    var result = +0;
    var i = 9;
    var temp = +string[i];
    while (!isNaN(temp)) // while temp is a number..
    {
        result = result * 10 + temp;
        i++;
        temp = +string[i];
    }
    return result;
}

