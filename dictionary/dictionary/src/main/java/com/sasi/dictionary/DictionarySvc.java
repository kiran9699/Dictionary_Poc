package com.sasi.dictionary;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Sasi
 */
@Service
public class DictionarySvc {

     private static List<String> list = null;
     @Autowired private RestTemplate restTemplate;

    static {
      // there will be local dictionary in systems at /usr/share/dict/words
      // --> this from MAC System please check this location: cat /usr/share/dict/words
      // Please check it for windows machine if you need it for windows
      final String pathName = "data/words";

      //final String pathName = "/usr/share/dict/words";
      //    --> If use mac get from this directory
      try {
        list = Files.readAllLines( new File(pathName).toPath(), Charset.defaultCharset() );
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

    public Boolean isEnglishWord(final String word) {
      final String requestUrl = "https://api.dictionaryapi.dev/api/v2/entries/en_US/";
      final TreeSet<String> finalWordsSet = new TreeSet<>();
      final TreeSet<String> generateEnglishWordSet = generateEnglishWords(word);
      System.out.println("generateEnglishWordSet: " + generateEnglishWordSet);
      final HttpHeaders httpHeaders = new HttpHeaders();
      HttpEntity<Object> entity = new HttpEntity<Object>(null, httpHeaders);
      try {
        generateEnglishWordSet.forEach(w -> {
          final HttpMethod httpMethod = HttpMethod.GET;
          System.out.println("requestUrl: " + requestUrl + w);
          ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl + w, httpMethod, entity, String.class);
          if(200 == responseEntity.getStatusCodeValue()) {
            finalWordsSet.add(w);
          }
        });
        System.out.println("finalWordsList: " + finalWordsSet);
        if(finalWordsSet.isEmpty()) {
          return false;
        }
        return true;
      } catch (final Exception e) {
        e.printStackTrace();
        return false;
      }
    }

  /**
   *
    * @param word
   * @return
   */
   public TreeSet<String> generateEnglishWords(String word) {
     final TreeSet<String> wordsSet = new TreeSet<>();
     word = word.toLowerCase();
     int[] freq = toFreq(word);
     List<String> lowercase = list.stream().map( s -> s.toLowerCase() ).
         filter( s->s.chars().allMatch(Character::isLetter)).collect( Collectors.toList() );
     for ( String l : lowercase ) {
       int[] freqIn = toFreq( l );
       if ( matches( freq, freqIn ) ) {
         if(list.contains(l)) {
           wordsSet.add(l);
         }
       }
     }
      return wordsSet;
    }


    /**
     * Returns true if all the frequencies of the letters match.
     *
     * @param freq
     * @param freqIn
     * @return
     */
    private static boolean matches(final int[] freq, final int[] freqIn) {
      for ( int i = 0; i < 26; i++ ) {
        if ( freq[i] == 0 && freqIn[i]>0) {
          return false;
        }
        else if (freq[i] < freqIn[i]) {
          return false;
        }
      }
      return true;
    }

    /**
     *
     * @param word
     * @return
     */
    private static int[] toFreq(String word) {
//      System.out.println("Given Word::: " + string);
      int[] freq = new int[26];
      for ( char c : word.toCharArray() ) {
        if ( ( c - 'a' ) >= 0 && ( c - 'a' ) < 26 ) {
          freq[c - 'a']++;
        }
      }
      return freq;
    }


}
