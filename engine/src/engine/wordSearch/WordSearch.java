package engine.wordSearch;

import engine.Card;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by eran on 17/04/2017.
 */
public class WordSearch implements Serializable{
    List<String> result;
    Trie trie;
    Set<String> currentRunWordCheck;

    public WordSearch(Set<String> words){
        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
    }

    public List<String > findWords(List<Card> board) {
        result = new LinkedList<>();
        int m = board.size();
        currentRunWordCheck = new HashSet<>();
        boolean[] visited = new boolean[m];
        AtomicInteger j = new AtomicInteger(0);
        board.stream().forEach(c->c.setIndex(j.getAndAdd(1)));
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

        if (!currentRunWordCheck.contains(str) && !trie.startsWith(str))
            return;

        currentRunWordCheck.add(str);
        if (trie.search(str)) {
                result.add(str);
        }

        visited[i] = true;

        List<Card> listOfCard = board.stream().distinct().collect(Collectors.toList());

        for (Card c : listOfCard){
            dfs(board,visited,str,c.getIndex(),trie);
        }
        //for (int j=0;j<i;j++)
         //   dfs(board, visited, str, j, trie);

        //for (int j=i+1;j<m;j++)
        //    dfs(board, visited, str, j, trie);

        visited[i] = false;
    }
}