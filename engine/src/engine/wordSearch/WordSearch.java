package engine.wordSearch;

import engine.Card;

import java.util.*;

/**
 * Created by eran on 17/04/2017.
 */
public class WordSearch {
    List<String> result;
    Trie trie;

    public WordSearch(Set<String> words){
        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
    }

    public List<String > findWords(Card[][] board) {
        result = new LinkedList<>();
        int m = board.length;
        int n = board[0].length;

        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(board, visited, "", i, j, trie);
            }
        }
        return result;
    }

    public void dfs(Card[][] board, boolean[][] visited, String str, int i, int j, Trie trie) {
        int m = board.length;
        int n = board[0].length;

        if (i < 0 || j < 0 || i >= m || j >= n) {
            return;
        }

        if (visited[i][j])
            return;

        str = str + board[i][j].getHiddenChar();

        if (!trie.startsWith(str))
            return;

        if (trie.search(str)) {
            result.add(str);
        }

        visited[i][j] = true;
        dfs(board, visited, str, i - 1, j, trie);
        dfs(board, visited, str, i + 1, j, trie);
        dfs(board, visited, str, i, j - 1, trie);
        dfs(board, visited, str, i, j + 1, trie);
        visited[i][j] = false;
    }
}