package engine.wordSearch;

import engine.Card;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by eran on 17/04/2017.
 */
public class WordSearch implements Serializable{
    List<String> result;
    Trie trie;
    Predicate<Card> filter;

    public WordSearch(Set<String> words){
        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
    }

    public List<String > findWords(List<Card> board) {
        result = new LinkedList<>();
        this.filter = filter;
        int m = board.size();

        boolean[] visited = new boolean[m];

        for (int i = 0; i < m; i++) {
                dfs(board, visited, "", i, trie);
        }
        return result;
    }

    public void dfs(List<Card> board, boolean[] visited, String str, int i,  Trie trie) {
        int m = board.size();

        if (i < 0 || i < 0 || i >= m) {
            return;
        }

        if (visited[i])
            return;


        str = str + board.get(i).getHiddenChar();

        if (!trie.startsWith(str))
            return;

        if (trie.search(str)) {
            if (str.length() >1)
                result.add(str);
        }

        visited[i] = true;
        dfs(board, visited, str, i - 1, trie);
        dfs(board, visited, str, i + 1, trie);
        visited[i] = false;
    }
}