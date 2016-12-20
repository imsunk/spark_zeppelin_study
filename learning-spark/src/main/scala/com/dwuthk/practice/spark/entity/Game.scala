package com.dwuthk.practice.spark.entity

import com.fasterxml.jackson.annotation.JsonProperty

case class Game(
  val id: Long,
  val developer: String,
  val name: String,
  val platform: String,
  val publisher: String,
  @JsonProperty("release_date") val releaseDate: String,
  val score: String)