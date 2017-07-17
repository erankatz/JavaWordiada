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

window.onload = function()
{
    checkLoginStatus();
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
    if (!e.target.classList.contains('leaveGame') && !e.target.classList.contains('close') && status === 'WaitingForPlayers' && !e.target.classList.contains('closeEnd'))
    {
        return false;
    }

    if (e.target.classList.contains('leaveGame') || e.target.classList.contains('close') || e.target.classList.contains('closeEnd') || e.target.classList.contains('replayButton'))
    {
        return true;
    }

    if (isMyTurn)
    {
        if (isButtonsEnabled)
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
                action: 'gameStatus'
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
                }

                isMyTurn = true;
                if (isComputer && isReplayOn)
                {
                    isMyTurn = false;
                }
                else if (isComputer)
                {
                    playComputerTurn();
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
                action: 'gameEnd'
            },
            type: 'GET',
            success: showEndGameDiaglogCallback
        }
    )
}

function showEndGameDiaglogCallback(json) {
    setReason(json.reason);
    var board = json.board;
    $('.highestScore')[0].innerHTML = json.winnersScore;
    var winnerDiv = $('.winnerDiv')[0];

    for (i = 0; i < json.winners.length; i++)
    {
        var winnerSpan = document.createElement('span');
        winnerSpan.classList.add('winnerName');
        winnerDiv.appendChild(winnerSpan);
        winnerSpan.innerHTML = winnerSpan.innerHTML + json.winners[i].m_Name + " ";
    }
    winnerSpan.innerHTML = winnerSpan.innerHTML + '.';

    createBoardForEndDialog(board);
}

function createBoardForEndDialog(board)
{
    var rows = board.m_Rows;
    var cols = board.m_Cols;
    var boardBody = $('.completedBoardBody');

    for (i=0; i<rows; i++)
    { // creates squares.
        rowDiv = $(document.createElement('div'));
        rowDiv.addClass('rowDiv');
        rowSquares = $(document.createElement('div'));
        rowSquares.addClass('rowSquares');
        rowSquares.appendTo(rowDiv);

        for (j=0; j<cols;j++)
        { // add the squares.
            squareDiv = $(document.createElement('div'));
            squareDiv.addClass('square');
            squareDiv.appendTo(rowSquares);

            color = board.m_Board[i][j].m_CellState;
            if (color === 'BLACK')
            {
                squareDiv.addClass('black');
            }
            else if (color ==='EMPTY')
            {
                squareDiv.addClass('empty');
            }
        }

        rowDiv.appendTo(boardBody);
    }
}

function setReason(reason)
{
    switch(reason)
    {
        case 'alone':
            $('.finishStatus')[0].innerHTML = 'You are the last player..';
            break;
        case 'moves':
            $('.finishStatus')[0].innerHTML = 'Moves Expired !!';
            break;
        case 'completed':
            $('.finishStatus')[0].innerHTML = 'Board was completed !';
            break;
        default:
            $('.finishStatus')[0].innerHTML = 'Unknown ..';
            break;
    }
}

function updatePlayersDetails()
{
    $.ajax
    (
        {
            url: 'games',
            data:
            {
                action: 'gamePlayers'
            },
            type: 'GET',
            success: updatePlayersDetailsCallback
        }
    )
}

function updatePlayersDetailsCallback(json)
{
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
                key: -1
            },
            type: 'GET',
            success: loadGameDetailsCallback
        }
    )
}

function rollDiceOnClick()
{
	$.ajax
	(
		{
			async: false,
            url: 'games',
            data:
            {
                action: 'rollDice',
				key: -1
            },
            type: 'GET',
            success: rollDiceResultCallback
		}
	
	)
}

function rollDiceResultCallback(json)
{
	diceResult = json.result;
	alert(json.msg);
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
    br=[];
	lettersFrequencyInDeckStrings.unshift("");
	var par = document.getElementsByClassName("lettersFrequencyInDeck")[0];
    for(c=0;c<lettersFrequencyInDeckStrings.length;c++){
        par.appendChild(document.createTextNode(lettersFrequencyInDeckStrings[c]));
        br[c]=document.createElement('br');
        par.appendChild(br[c]);
    }
    br=[];
    par = document.getElementsByClassName('lowestFrequencyDictionaryWords')[0];
    for(c=0;c<lowestFrequencyDictionaryWordsStrings.length;c++){
        par.appendChild(document.createTextNode(lowestFrequencyDictionaryWordsStrings[c]));
        br[c]=document.createElement('br');
        par.appendChild(br[c]);
    }

    createBoard(json.rows, json.cols);
}

function onClickMainBoardCell(event){
	    alert("2")
}

function createBoard(rows, cols) {
    var board = document.getElementById("mainBoardBody");
    board.innerHTML= "";
    for (i = 0; i < rows; i++) { // creates squares + row blocks.
        var row = board.insertRow(i);

        for (j = 0; j < cols; j++) { // add the squares.
            var cell = row.insertCell(j);
            cell.innerHTML = "?";
			cell.rowIndex = i;
			cell.colIndex = j;
			cell.classList.add('square');
        }
    }
	$('.square').click(function(event) {
	    alert("Row :" + event.rowIndex + " Col: " + event.colIndex);
	});
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
            action: "leaveGame"
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
                action: 'pageDetails'
            },
            type: 'GET',
            success: turnPlayCallback
        }
    )
}

function turnPlayCallback(json)
{
    var currentMove = json.move;
    var totalMoves = $('.totalMoves')[0].innerHTML;
    if (totalMoves != undefined && totalMoves < currentMove)
    {
        currentMove--;
    }

    $('.scoreSpan')[0].innerHTML = json.score;
    $('.currentMove')[0].innerHTML = currentMove;
    $('.undoSpan')[0].innerHTML = json.undo;
    $('.turnSpan')[0].innerHTML = json.turn;

    if (isMyTurn)
    {
        $('.turnSpan')[0].innerHTML = json.turn;
    }
    else
    {
        $('.turnSpan')[0].innerHTML = '0';
    }

    var color;
    var board = json.board.m_Board;
    var square;
    turn = json.turn;
    for (i=0; i<board.length; i++)
    {
        for (j=0; j<board[0].length; j++)
        {
            square = $('.square[row="' + i + '"][col="' + j + '"]')[0];
            removeClass(square);
            color = board[i][j].m_CellState;
            if (color === 'BLACK')
            {
                square.classList.add('black');
            }
            else if (color ==='EMPTY')
            {
                square.classList.add('empty');
            }
            else
            {
                //nothing..
            }
        }
    }
    if (isMyTurn)
    {
        setPerfectRows(json.perfectRows);
        setPerfectCols(json.perfectCols);
    }
}

function setPerfectRows(perfectRows)
{
    if (perfectRows === undefined)
    {
        return;
    }

    for (i=0; i<perfectRows.length; i++)
    {
        for (j=0; j<perfectRows[0].length; j++)
        {
            var hint = $('.rowHint[row="' + i + '"][col="' + j + '"]')[0];
            if (hint != undefined && hint.classList.contains('perfect'))
            {
                hint.classList.remove('perfect');
            }

            if (perfectRows[i][j])
            {
                hint.classList.add('perfect');
            }
        }
    }
}

function setPerfectCols(perfectCols)
{
    if (perfectCols === undefined)
    {
        return;
    }

    for (i=0; i<perfectCols.length; i++)
    {
        for (j=0; j<perfectCols[0].length; j++)
        {
            var hint = $('.colHint[row="' + j + '"][col="' + i + '"]')[0];
            if (hint != undefined && hint.classList.contains('perfect'))
            {
                hint.classList.remove('perfect');
            }

            if (perfectCols[i][j])
            {
                hint.classList.add('perfect');
            }
        }
    }
}

function removeClass(square)
{
    if (square.classList.contains('black'))
    {
        square.classList.remove('black');
    }
    else if (square.classList.contains('empty'))
    {
        square.classList.remove('empty');
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

function playComputerTurn()
{
    $.ajax
    (
        {
            async: false,
            url: 'games',
            data:
            {
                action: 'computerTurn'
            },
            type: 'POST',
            success: updateGamePage
        }
    );
    onEndMoveClick();
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


