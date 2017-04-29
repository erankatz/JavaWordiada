package engine.wordSearch;

import java.io.Serializable;
import java.util.*;

/**
 * Created by eran on 17/04/2017.
 */
public class Trie implements Serializable{
    class TrieNode implements Serializable{
        public Map<Character,TrieNode> children = new HashMap();
        public String item = "";
    }

    public TrieNode root = new TrieNode();

    public void insert(String word){
        TrieNode node = root;
        for(char c: word.toCharArray()){
            if(node.children.get(c) == null){
                node.children.put(c,new TrieNode());
            }
            node = node.children.get(c);
        }
        node.item = word;
    }

    public boolean search(String word){
        TrieNode node = root;
        for(char c: word.toCharArray()){
            if(node.children.get(c)==null)
                return false;
            node = node.children.get(c);
        }
        if(node.item.equals(word)){
            return true;
        }else{
            return false;
        }
    }

    public boolean startsWith(String prefix){
        TrieNode node = root;
        for(char c: prefix.toCharArray()){
            if(node.children.get(c)==null)
                return false;
            node = node.children.get(c);
        }
        return true;
    }
}