package yuji.software.openbd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenBDBookTest {
    @Test
    void shouldDeserializeSampleJson() throws Exception {
        String json = """
                [
                  {
                    "onix": {
                      "CollateralDetail": {},
                      "RecordReference": "9784091295491",
                      "NotificationType": "03",
                      "ProductIdentifier": {
                        "ProductIDType": "15",
                        "IDValue": "9784091295491"
                      },
                      "DescriptiveDetail": {
                        "TitleDetail": {
                          "TitleType": "01",
                          "TitleElement": {
                            "TitleElementLevel": "01",
                            "TitleText": {
                              "collationkey": "ギン ノ サジ",
                              "content": "銀の匙 = Silver Spoon. VOLUME15"
                            }
                          }
                        },
                        "Contributor": [
                          {
                            "SequenceNumber": "1",
                            "ContributorRole": [],
                            "PersonName": {
                              "content": "荒川, 弘, 1973-",
                              "collationkey": "アラカワ, ヒロム, 1973-"
                            }
                          }
                        ],
                        "Collection": {
                          "CollectionType": "10",
                          "TitleDetail": {
                            "TitleType": "01",
                            "TitleElement": [
                              {
                                "TitleElementLevel": "02",
                                "PartNumber": "",
                                "TitleText": {
                                  "collationkey": "",
                                  "content": "少年サンデーコミックス"
                                }
                              }
                            ]
                          }
                        }
                      },
                      "PublishingDetail": {
                        "Imprint": {
                          "ImprintName": "小学館"
                        },
                        "PublishingDate": [
                          {
                            "PublishingDateRole": "11",
                            "Date": "202002"
                          }
                        ]
                      },
                      "ProductSupply": {
                        "SupplyDetail": {
                          "ProductAvailability": "99",
                          "Price": [
                            {
                              "PriceType": "01",
                              "CurrencyCode": "JPY",
                              "PriceAmount": "454"
                            }
                          ]
                        }
                      }
                    },
                    "hanmoto": {
                      "hastameshiyomi": true,
                      "datemodified": "2026-02-17 05:01:02",
                      "datecreated": "2020-01-16 10:25:05",
                      "datekoukai": "2020-01-16"
                    },
                    "summary": {
                      "isbn": "9784091295491",
                      "title": "銀の匙 = Silver Spoon. VOLUME15",
                      "volume": "",
                      "series": "少年サンデーコミックス",
                      "publisher": "小学館",
                      "pubdate": "202002",
                      "cover": "",
                      "author": "荒川,弘,1973-"
                    }
                  }
                ]
                """;

        ObjectMapper mapper = new ObjectMapper();
        List<OpenBDBook> books = mapper.readValue(json, new TypeReference<>() {
        });

        assertEquals(1, books.size());
        assertEquals("銀の匙 = Silver Spoon. VOLUME15", books.getFirst().summary().title());
        assertEquals("荒川,弘,1973-", books.getFirst().summary().author());
        assertEquals("銀の匙 = Silver Spoon. VOLUME15", books.getFirst().onix().descriptiveDetail().titleDetail().titleElement().titleText().content());
    }
}
