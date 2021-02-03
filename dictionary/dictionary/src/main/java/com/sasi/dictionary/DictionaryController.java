package com.sasi.dictionary;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author niranjanjarugu
 */
@RestController
public class DictionaryController {

  @Autowired private DictionarySvc dictionarySvc;

  @GetMapping(value = "/isEnglishWord/{englishWord}",
      produces = "application/json")
  public String isEnglishWord(@PathVariable final String englishWord) throws Exception{
    final JSONObject responseObject = new JSONObject();
    final Boolean isEnglishWord = dictionarySvc.isEnglishWord(englishWord);
    responseObject.put("isEnglishWord", isEnglishWord);
    return isEnglishWord.toString();
  }

}
