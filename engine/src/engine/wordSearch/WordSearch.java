package engine.wordSearch;

import engine.Card;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        return result.stream().filter(c->c.length()>1).collect(Collectors.toList());
    }

    public void dfs(List<Card> board, boolean[] visited, String str, int i,  Trie trie) {
        int m = board.size();

        if (i < 0 || i >= m) {
            return;
        }

        if (visited[i])
            return;


        str = str + board.get(i).getHiddenChar();

        if (!trie.startsWith(str))
            return;

        if (trie.search(str)) {
                result.add(str);
        }

        visited[i] = true;

        for (int j=0;j<i;j++)
            dfs(board, visited, str, j, trie);

        for (int j=i+1;j<m;j++)
            dfs(board, visited, str, j, trie);

        visited[i] = false;
    }
}