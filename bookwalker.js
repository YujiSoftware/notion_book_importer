(async function(){
  function download(items) {
      const blob = new Blob([JSON.stringify(items)], { type: "text/javascript", endings: "native" });
      const link = document.createElement('a');
      link.download = 'bookwalker.json';
      link.href = URL.createObjectURL(blob);
      link.click();
      URL.revokeObjectURL(link.href);
  };

  let readStatus = {
    null: "未読",
    0: "途中",
    1: "既読",
  };
  let page = 1;
  let items = [];

  while (true) {
    const formData = new FormData();
    formData.append("holdBook-series", "0");
    formData.append("page", page);
    formData.append("csrfToken", window.BW_CSRF_TOKEN);

    const response = await fetch("https://bookwalker.jp/prx/holdBooks-api/hold-book-list/", {
      method: "POST",
      body: formData,
    });
    const json = await response.json();
    for (const entity of json.holdBookList.entities) {
      items.push({
        "title": entity.title,
        "authors": entity.authors.map(a => a.authorName),
        "category": entity.categoryName,
        "label": entity.labelName,
        "company": entity.companyName,
        "status": readStatus[entity.readFlag.readFinishFlag],
        "buyTime": entity.buyTime,
        "url": entity.viewerUrl,
        "uuid": entity.uuid,
      });
    }

    console.log({"page": page, "totalPage": json.holdBookList.totalPage});
    if (page >= json.holdBookList.totalPage) {
      break;
    }

    page++;
  }
  download({
    store: "BOOK_WALKER",
    createdAt: new Date(),
    items: items,
  });
})()