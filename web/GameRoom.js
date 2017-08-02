/**
 * Created by user on 18/10/2016.
 */

var status;
var userName;
var isComputer;
var turn = 0;
var intervalTimer = 2000;
var isMyTurn = false;
var isButtonsEnabled = true;
var isEnabledSaver = true;
var showScoreBoard;
var myTurnSaver = false;
var isFirstStatus = true;
var isReplayOn = false;
var diceResult = null;
var gameTitle = null;
var pendingAction = null;
var gameId = null;
var CurrentchatVersion = 0;

window.onload = function()
{
	var url = new URL(window.location);
	gameId = url.searchParams.get("id");
    checkLoginStatus();
};

function onCheckSelectedWord(){
	if (pendingAction == "SELECTWORD"){
		$.ajax
		(
			{
				url: 'games',
				data:
				{
					action: 'CheckSelectedWord',
					key: gameId,
				},
				type: 'GET',
				success: CheckSelectedWordCallBack
			}
		)
	}
}

function getLettersFrequencyInDeckCallBack(json)
{
	var lettersFrequencyInDeckStrings = json.lettersFrequencyInDeck.split("\n");
	lettersFrequencyInDeckStrings.unshift("");
	var par = document.getElementsByClassName("lettersFrequencyInDeck")[0];
    for(c=0;c<lettersFrequencyInDeckStrings.length;c++){
		var line = lettersFrequencyInDeckStrings[c].split("-");
		while (line[0].trim(" ") != par.rows.item(c).cells.item(0).innerText.trim(" "))
		{
			par.rows.item(c).parentElement.removeChild(par.rows.item(c))
		}
		for (j = 0; j < line.length; j++) { // add the squares.
            par.rows.item(c).cells[j].innerText = line[j];
		}
    }
}


function updateLettersFrequencyInDeck()
{
	$.ajax
		(
			{
				url: 'games',
				data:
				{
					action: 'gameDetails',
					key: gameId,
				},
				type: 'GET',
				success: getLettersFrequencyInDeckCallBack
			}
		)
}


function CheckSelectedWordCallBack(json){
	if (json.isValidWord){
		updateLettersFrequencyInDeck();
	}
	window.alert(json.currentPlayerMessage)
}

function onRevealCards(){
	if (pendingAction == "REVEALCARDS")
	{
		$.ajax
		(
			{
				url: 'games',
				data:
				{
					action: 'revealCards',
					key: gameId,
					row: event.toElement.rowIndex +1,
					col: event.toElement.colIndex +1
				},
				type: 'GET',
				success: onRevealCardsCallBack
			}
		)
	} else{
		alert("key already pressed");
	}
}
function onRevealCardsCallBack(json){
	if (json.isSuccess){
		updateGamePage();
	} else {
		if (json.currentPlayerMsg != undefined){
			alert(json.currentPlayerMsg);
		}
	}
}

	function onClickMainBoardCell(event){
	$.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'selectCard',
				key: gameId,
				row: event.toElement.rowIndex +1,
				col: event.toElement.colIndex +1
            },
            type: 'POST',
            success: updateGamePage
        }
    )
}

function onClearCardSelection(){
	$.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'clearCardSelection',
				key: gameId
            },
            type: 'POST',
            success: updateGamePage
        }
    )
}

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


function initializePage() {
    userName = getUserName();
    isComputer = isUserComputer();
    isButtonsEnabled = true;
    showScoreBoard = true;
    isMyTurn = false;
    status = 'WaitingForPlayers';
    loadWindowDetails();
    gameStatus();

    setInterval(checkLoginStatus, intervalTimer);
    setInterval(updatePlayersDetails, intervalTimer);
    setInterval(gameStatus, intervalTimer);
}

function statusCallback(json)
{
    if (!json.isConnected)
    {
        window.location = "index.html";
    }
    else if (json.gameNumber === -1)
    {
        window.location = "LobbyPage.html";
    }
    else if (isFirstStatus)
    {
        isFirstStatus = false;
        initializePage();
    }
}

document.addEventListener("click",clickHandler,true);

function clickHandler(e)
{
    if (!isButtonAvailable(e))
    {
        e.stopPropagation();
        e.preventDefault();
    }
}

function isButtonAvailable(e)
{
    if (!e.target.classList.contains('leaveGame') && !e.target.classList.contains('close') && status === 'WaitingForPlayers' && !e.target.classList.contains('closeEnd') && !(e.target.value == "Send"))
    {
        return false;
    }

    if (e.target.classList.contains('leaveGame') || e.target.classList.contains('close') || e.target.classList.contains('closeEnd') || e.target.classList.contains('replayButton') || (e.target.value == "Send"))
    {
        return true;
    }

    if (isMyTurn)
    {
        if (isButtonsEnabled && !isComputer)
        {
            return true;
        }
        else // buttons are Disabled.
        {
            return false;
        }
    }
    else //not my turn.
    {
        if (isButtonsEnabled)
        {
            if (e.target.classList.contains('moveList') || e.target.classList.contains('specialButton'))
            {
                return true;
            }
        }
        else // buttons are Disabled.
        {
            return false;
        }
    }
}

function gameStatus()
{
    $.ajax
    (
        {
            async: false,
            url: 'games',
            data:
            {
                action: 'gameStatus',
				key: gameId
            },
            type: 'GET',
            success: handleStatus
        }
    )
}

function handleStatus(json)
{

    newStatus = json.status;
    playerTurn = json.currentPlayerTurnName;
	pendingAction = json.pendingAction;
    switch(newStatus)
    {
        case 'WaitingForPlayers':
            status = newStatus;
            break;
        case 'Running':
            if (!isReplayOn)
            {
                updateGamePage();
            }

            $('.currentPlayerName')[0].innerHTML = json.currentPlayerTurnName;
            if (status === 'WaitingForPlayers')
            {
                alert('Let the Game BEGIN !!!!');
            }

            if (!isMyTurn && playerTurn === userName)
            {
                if (!isComputer)
                {
                    alert('Hey Buddy! it is now your turn !');
					diceResult = null;
					revealCardsPending = false;
					checkSelectedWordPending = false;
                }

                isMyTurn = true;
                if (isComputer && isReplayOn)
                {
                    isMyTurn = false;
                }
                
            }

            if (isMyTurn && playerTurn != userName)
            {
                alert('It is your turn, but server says its someone else turn ...');
                isMyTurn = false;
            }
            status = newStatus;
            break;
        case "Finished":
            isMyTurn = false;
            if (showScoreBoard) {
                showEndGameDiaglog();
                showScoreBoard = false;
            }
            status = newStatus;
            break;
    }
    $('.gameStatus').text('Game status: ' + status);
}

function showEndGameDiaglog()
{
    $('.winnerDialog')[0].style.display = "inline-block";
    $.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'gameEnd',
				key: gameId
            },
            type: 'GET',
            success: showEndGameDiaglogCallback
        }
    )
}

function showEndGameDiaglogCallback(json) {
    var board = json.board;
    $('.highestScore')[0].innerHTML = json.winnersScore;
    var winnerDiv = $('.winnerDiv')[0];

    for (i = 0; i < json.winners.length; i++)
    {
        var winnerSpan = document.createElement('span');
        winnerSpan.classList.add('winnerName');
        winnerDiv.appendChild(winnerSpan);
        winnerSpan.innerHTML = winnerSpan.innerHTML + json.winners[i].name + " ";
    }
    winnerSpan.innerHTML = winnerSpan.innerHTML + '.';
	
    createBoardForEndDialog(board.cards);

}

function updatePlayersDetails()
{
    $.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'gamePlayers',
				key: gameId
            },
            type: 'GET',
            success: updatePlayersDetailsCallback
        }
    )
}

function updatePlayersDetailsCallback(json)
{
	json = JSON.parse(json)
    $('.registeredPlayers').text(json.length);

    var playersNamesDiv = $('.playersNamesBody');
    var playersScoreDiv = $('.playersScoreBody');
    var playersTypeDiv = $('.playersTypesBody');


    playersNamesDiv.empty();
    playersTypeDiv.empty();
    playersScoreDiv.empty();
    for (i=0; i<json.length; i++)
    {
        var playerContainerDiv = $(document.createElement('div'));
        playerContainerDiv.addClass('playerContainerDiv');
        playerContainerDiv.appendTo(playersNamesDiv);

        var playerDiv = $(document.createElement('div'));
        playerDiv.addClass('playerDiv');
        playerDiv.appendTo(playerContainerDiv);

        var scoreDiv = $(document.createElement('div'));
        scoreDiv.addClass('scoreDiv');
        scoreDiv.appendTo(playersScoreDiv);

        var typeDiv = $(document.createElement('div'));
        typeDiv.addClass('typeDiv');
        typeDiv.appendTo(playersTypeDiv);
    }

    var playerDivs = $('.playerDiv');
    var scoreDivs = $('.scoreDiv');
    var typeDivs = $('.typeDiv');
    for (i=0; i<json.length; i++)
    {
        playerDivs[i].innerHTML = json[i].name;
        scoreDivs[i].innerHTML = json[i].score;
        typeDivs[i].innerHTML = json[i].type;
    }
}

function loadWindowDetails()
{
    $('.userNameSpan').text('Hello, '+ userName + " playing as "+ (isComputer ? "computer" : "human") + ", enjoy playing.");
    loadGameDetails();
}

function loadGameDetails()
{
    $.ajax
    (
        {
            async: false,
            url: 'games',
            data:
            {
                action: 'gameDetails',
                key: gameId
            },
            type: 'GET',
            success: loadGameDetailsCallback
        }
    )
}

function rollDiceOnClick()
{
	if (pendingAction == "ROLLDICE"){
		$.ajax
		(
			{
				async: false,
				url: 'games',
				data:
				{
					action: 'rollDice',
					key: gameId
				},
				type: 'GET',
				success: rollDiceResultCallback
			}
		)
	} else{
		alert("dice already rolled got " + diceResult);
	}

}

function rollDiceResultCallback(json)
{
	diceResult = json.result;
	alert(json.msg);
	revealCardsPending  = true;
}

function loadGameDetailsCallback(json)
{
    var key = json.key;
    var creatorName = json.creatorName;
    var gameName = json.gameTitle;
	gameTitle = json.gameTitle;
    var boardSize = json.rows + " X " + json.cols;
    var lettersFrequencyInDeckStrings = json.lettersFrequencyInDeck.split("\n");
    var lowestFrequencyDictionaryWordsStrings = json.lowestFrequencyDictionaryWords.split("\n");

    $('.key').text("Game id: " + key + ".");
    $('.creatorName').text("Game Creator: " + creatorName + ".");
    $('.gameName').text("Game Title: " + gameName);
    $('.boardSize').text("Board size: " + boardSize);
    $('.moves').text("Round number: 0");
    $('.registeredPlayers').text(json.registeredPlayers);
    $('.requiredPlayers').text(json.requiredPlayers);
	$('.isGoldFishMode').text("is Gold Fish :" + json.isGoldFishMode);
	$('scoreMode').text("Score Mode: " + json.scoreMode);
	lettersFrequencyInDeckStrings.unshift("");
	var par = document.getElementsByClassName("lettersFrequencyInDeck")[0];
    for(c=0;c<lettersFrequencyInDeckStrings.length;c++){
		var row = par.insertRow(c);
		var line = lettersFrequencyInDeckStrings[c].split("-");
		for (j = 0; j < line.length; j++) { // add the squares.
            var cell = row.insertCell(j);
			cell.innerText = line[j];
		}
    }
    br=[];
    par = document.getElementsByClassName('lowestFrequencyDictionaryWords')[0];
    for(c=0;c<lowestFrequencyDictionaryWordsStrings.length;c++){
        par.appendChild(document.createTextNode(lowestFrequencyDictionaryWordsStrings[c]));
        br[c]=document.createElement('br');
        par.appendChild(br[c]);
    }

    createBoard(json.rows, json.cols);
	updateGamePage();
}

function createBoardForEndDialog(board) {
    var htmlBoard = document.getElementsByClassName("completedBoardBody")[0];
    htmlBoard.innerHTML = "";
    for (i = 0; i < board.length; i++) { // creates squares + row blocks.
        var row = htmlBoard.insertRow(i);

        for (j = 0; j < board.length; j++) { // add the squares.
            var cell = row.insertCell(j);
            cell.innerText = board[i][j].letter;
            cell.rowIndex = i;
            cell.colIndex = j;
			if (board[i][j] == null){
				cell.classList.add('squreStyleRemoved');
			} else{
				if (board[i][j].isSelected) {
					cell.classList.add('squreStyleSelected');
				} else if (board[i][j].revealed) {
					cell.classList.add('squreStyleRevealed');
				} else {
					cell.classList.add('square');
				}
				if (board[i][j].revealed && cell.innerText != board[i][j].letter) {
					cell.innerText = board[i][j].letter;
				}
				if (!board[i][j].revealed) {
					cell.innerText = "?";
				}
			}
        }
    }
}


function createBoard(rows, cols) {
    var board = document.getElementById("mainBoardBody");
    board.innerHTML= "";
    for (i = 0; i < rows; i++) { // creates squares + row blocks.
        var row = board.insertRow(i);

        for (j = 0; j < cols; j++) { // add the squares.
            var cell = row.insertCell(j);
            cell.innerText = "?";
			cell.rowIndex = i;
			cell.colIndex = j;
			cell.classList.add('square');
			cell.addEventListener("click", onClickMainBoardCell);
        }
    }
}

function onSquareClick(event)
{
    if (event.target.classList.contains('selected'))
    {
        event.target.classList.remove('selected')
    }
    else
    {
        event.target.classList.add('selected');
    }
}

function onColorChooserClick(event)
{
    if (event.target.classList.contains('colorSelected'))
    {
        event.target.classList.remove('colorSelected');
    }
    else
    {
        var colorChoosers = $('.colorChooserDiv').children();
        for (i=0; i<colorChoosers.length; i++)
        {
            if (colorChoosers[i].classList.contains('colorSelected'))
            {
                colorChoosers[i].classList.remove('colorSelected');
            }
        }
        event.target.classList.add('colorSelected');
    }
}

function getUserName()
{
    var result;
    $.ajax
    ({
        async: false,
        url: 'login',
        data: {
            action: "status"
        },
        type: 'GET',
        success: function(json) {
            result = json.userName;
        }
    });
    return result;
}

function isUserComputer()
{
    var result;
    $.ajax
    ({
        async: false,
        url: 'login',
        data: {
            action: "status"
        },
        type: 'GET',
        success: function(json) {
            result = json.isComputer;
        }
    });
    return result;
}

function onLeaveGameClick()
{
    $.ajax
    ({
        async: false,
        url: 'games',
        data: {
            action: "leaveGame",
			key: gameId
        },
        type: 'GET',
        success: function() {
            window.location = "LobbyPage.html";
        }
    });
}


function updateGamePage()
{
    $.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'pageDetails',
				key: gameId
            },
            type: 'GET',
            success: turnPlayCallback
        }
    )
}

function turnPlayCallback(json)
{
    var currentMove = json.move;
    //var totalMoves = $('.totalMoves')[0].innerHTML;
    //if (totalMoves != undefined && totalMoves < currentMove)
    //{
    //    currentMove--;
    //}

    $('.scoreSpan')[0].innerHTML = json.score;
    $('.turnSpan')[0].innerHTML = json.turn;
	$('.moves')[0].innerText = "Round number: " + json.move
    if (isMyTurn)
    {
        $('.turnSpan')[0].innerHTML = json.turn;
    }
    else
    {
        $('.turnSpan')[0].innerHTML = '0';
    }

    var board = json.board.cards;
    var cell;
    turn = json.turn;
	var htmlBoard = document.getElementById("mainBoardBody");
	if (htmlBoard.rows.length == 0)
	{
		loadGameDetails()
	}
    for (i=0; i<board.length; i++)
    {
		var htmlRow = htmlBoard.rows.item(i);
        for (j=0; j<board[0].length; j++)
        {
            cell = htmlRow.cells.item(j);
            removeClass(cell);
			if (board[i][j] == null)
			{
				cell.classList.add(squreStyleRemoved);
			} else {
				if (board[i][j].isSelected){
					cell.classList.add('squreStyleSelected');
				}else if (board[i][j].revealed){
					cell.classList.add('squreStyleRevealed');
				} else{
					cell.classList.add('square');
				}
				if (board[i][j].revealed && cell.innerText != board[i][j].letter){
					cell.innerText = board[i][j].letter;
				}
				if (!board[i][j].revealed){
					cell.innerText = "?";
				}
			}
        }
    }
    if (isMyTurn)
    {

	}
}


function removeClass(square)
{
    if (square.classList.contains('squre'))
    {
        square.classList.remove('squre');
    }
    if (square.classList.contains('squreStyleSelected'))
    {
        square.classList.remove('squreStyleSelected');
    }
	if (square.classList.contains('squreStyleRevealed')){
		square.classList.remove('squreStyleRevealed');
	}
	if (square.classList.contains('squreStyleRemoved')){
		square.classList.remove('squreStyleRemoved');
	}
}

function getChooserColor()
{
    var color = $('.colorSelected')[0];
    if (color == undefined)
    {
        return undefined;
    }

    if (color.classList.contains('blackChooser'))
    {
        return 'black';
    }
    else if (color.classList.contains('whiteChooser'))
    {
        return 'empty';
    }
    else if (color.classList.contains('undefinedChooser'))
    {
        return 'undefined';
    }
    else
    {
        return undefined;
    }
}

function onEndMoveClick()
{
    isMyTurn = false;
    turn = 0;
    $.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'endMove'
            },
            type: 'POST',
            success: updateGamePage
        }
    )
}

function onUndoClick()
{
    $.ajax
    (
        {
            async: false,
            url: 'games',
            data:
            {
                action: 'undoMove'
            },
            type: 'POST',
            success: updateGamePage
        }
    )
}

function onScoreListDetailsClick()
{
    $.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'moveList'
            },
            type: 'GET',
            success: onScoreListDetailsClickCallback
        }
    )
}

function onScoreListDetailsClickCallback(json)
{
    isButtonsEnabled = false;
    $('.scoreListDetailsDialog')[0].style.display = "inline-block";

    //var text;
    //var div;
    //body.contents().remove();
    //
    //if (json.moves.length === 0)
    //{
    //    text = 'There are no moves to show ..';
    //    div = $(document.createElement('div')).text(text);
    //    div.appendTo(body);
    //}
    //
    //for (i=0; i<json.moves.length; i++)
    //{
    //    text = json.moves[i];
    //    div = $(document.createElement('div')).text(text);
    //    div.appendTo(body);
    //}
}

function removeDialog(event)
{
    event.target.parentElement.parentElement.style.display = "none";
    $('.scoreListDetailsDialog')[0].style.display = "none";
    if (event.target.parentElement.parentElement != undefined && event.target.parentElement.parentElement.classList.contains('scoreListDetailsDialog'))
    {
        isButtonsEnabled = true;
    }
}



function onReplayClick()
{
    isReplayOn = true;
    isEnabledSaver = isButtonsEnabled;
    isButtonsEnabled = false;

    $.ajax
    (
        {
            async: false,
            url: 'games',
            data:
            {
                action: 'replay'
            },
            type: 'GET',
            success: replayCallback
        }
    );
}

function onReplayClose()
{
    $('.replayDialog')[0].style.display = "none";
    isButtonsEnabled = isEnabledSaver;
    boardBody = $('.boardBody')[0];
    board = $('.board')[0];
    board.appendChild(boardBody);
    isReplayOn = false;
    updateGamePage();
}

var actions;
var maxIndex;
var index;

function replayCallback(json)
{
    $('.replayDialog')[0].style.display = "inline-block";
    boardBody = boardBody = $('.boardBody')[0];;
    replayBoard = $('.replayBoardBody')[0];
    replayBoard.appendChild(boardBody);
    resetBoard();
    actions = json.actions;
    index = 0;
    maxIndex = actions.length;
    $('.currentTurnReplay')[0].innerHTML = '0';
    $('.totalTurnReplay')[0].innerHTML = actions.length;
}

function resetBoard()
{
    var squares = $('.square');
    for (i=0; i<squares.length; i++)
    {
        sqr = squares[i];
        if (sqr.classList.contains('black')) {
            sqr.classList.remove('black');
        }

        if (sqr.classList.contains('empty')) {
            sqr.classList.remove('empty');
        }

        if (sqr.classList.contains('selected')) {
            sqr.classList.remove('selected');
        }
    }

    var perfects = $('.perfect');
    for (i=0; i<perfects.length; i++)
    {
        perfects[i].classList.remove('perfect');
    }
}

function onNextClick()
{
    if (index < maxIndex)
    {
        color = actions[index].m_State.toLowerCase();
        cells = actions[index].changedIndexes;
        for (i = 0; i < cells.length; i++) {
            row = cells[i].key;
            col = cells[i].value;
            square = $('.square[row="' + row + '"][col="' + col + '"]')[0];
            removeClass(square);
            if (color === 'empty' || color === 'black')
            {
                square.classList.add(color);
            }
        }
        index++;
        $('.currentTurnReplay')[0].innerHTML = index;

    }
}

function onPrevClick()
{
    if (index > 0)
    {
        index--;
        $('.currentTurnReplay')[0].innerHTML = index;
        cells = actions[index].changedIndexes;
        for (i = 0; i < cells.length; i++)
        {
            color = actions[index].oldStates[i].toLowerCase();
            row = cells[i].key;
            col = cells[i].value;
            square = $('.square[row="' + row + '"][col="' + col + '"]')[0];
            removeClass(square);
            if (color === 'empty' || color === 'black')
            {
                square.classList.add(color);
            }
        }
    }
}


//entries = the added chat strings represented as a single string
function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");
    
    // add the relevant entries
    $.each(entries || [], appendChatEntry);
    
    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<span class='smiley'></span>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}


//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well
function ajaxChatContent() {
    $.ajax({
        url: 'games',
		data:{
			action: 'chatContent',
			key: gameId,
			chatVersion: CurrentchatVersion
		},
		type:'GET',
		success: function(data) {
            /*
             data is of the next form:
             {
                "entries": [
                    {
                        "chatString":"Hi",
                        "username":"bbb",
                        "time":1485548397514
                    },
                    {
                        "chatString":"Hello",
                        "username":"bbb",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             */
			data = JSON.parse(data)
            if (data.version !== CurrentchatVersion) {
				console.log("Server chat version: " + data.version + ", Current chat version: " + CurrentchatVersion);
                CurrentchatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do
    //add a function to the submit event
    $("#chatform").submit(function() {
        $.ajax({
			url: 'games',
			data:{
				action: "sendMessage",
				key: gameId,
				chatString: $("#userstring")[0].value
			},
            type: 'POST',
            success: function(r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
            }
        });

        $("#userstring").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
	});
});

function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, intervalTimer);
}

//activate the timer calls after the page is loaded
$(function() {

    //prevent IE from caching ajax calls
    //$.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxChatContent();
});
