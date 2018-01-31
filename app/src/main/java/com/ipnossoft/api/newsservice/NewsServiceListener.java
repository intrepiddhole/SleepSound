package com.ipnossoft.api.newsservice;

import com.ipnossoft.api.newsservice.model.News;
import java.util.List;

public interface NewsServiceListener {
  void newsServiceDidFailFetchingNews(Exception var1);

  void newsServiceDidFinishFetchingNews(List<News> var1);
}