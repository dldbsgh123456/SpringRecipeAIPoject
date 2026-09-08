package com.sist.web.controller;

import org.springframework.stereotype.Controller;
/*
 *    전체 구조 : HTML (재료 선택) => 사용자
 *                |
 *          RecipeController : 화면 출력
 *                |
 *             재료선택
 *                |
 *        RecipeRestController
 *                |
 *       -----------------------
 *         RecipeService
 *         Mapper 
 *       -----------------------
 *           |
 *       EmbeddingModel
 *       String -> float[]
 *         |
 *     ------------------------
 *       vector => [0.1,0.2....]
 *     ------------------------
 *         |
 *      Mapper
 *        => findSimilarRecipe() => 유사도
 *         |
 *    -------------------------
 *      PostgreSQL + pgVector
 *      embedding => 검색
 *      Cosine Distance => 가까운 거리 측정 (실수)
 *                         String => float 
 *    -------------------------
 *         | => vectorDB
 *     distance => 작은 순 : 맛집 / 쇼핑몰
 *         |
 *     ORDER BY => LIMIT 5
 *         |
 *    ----------------------
 *     재료 비교
 *      |
 *     AVA(보유) / SHORTAGE(부족) / SUB(대체) 
 *    
 *    ----------------------
 *        |
 *     ingredientRate
 *     ingredients
 *     missingingredient
 *    ----------------------
 *        |
 *     ThymeLeaf
 *       => 충족률 : 75%
 *       => 재료 .... (X)
 * 
 */
@Controller
public class RecipeRestController {

}
