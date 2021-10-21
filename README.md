# 16조 FaceBookClone Backend

### Team
+ Frontend : 김현수,강지훈,김동우 (REACT)
+ Backend :  오준석,이정인 (SPRING)

#### Project Name
F : FaceBook의 F라도 따라가자

#### Objective
1. Frontend와 Backend 다른환경에서의 연동(CORS)
2. 회원가입 & Spring에서 JWT 방식의 로그인
3. 게시판 구현(CRUD 적용)
4. 댓글 작성(CRUD 적용)
5. 좋아요 기능

### Project Collaboration Process
<details markdown = "1">
<summary>
API설계
</summary>
 <div style="width:700px; margin: auto" >

[NOTION](https://www.notion.so/5-0b6bbc932fe3490093273e632f312d9f) 

### 로그인/회원가입
|기능　　　　　|Method|URL|Request|Response|
|---|---|---|---|---|
|로그인|POST|/user/login|{<br>userId: userId<br>pwd: pwd<br>}|{<br>statusCode : 200<br>responseMessage: 로그인 성공<br>jwtToken: jwtToken,<br>userId: userId<br>}|
|회원가입|POST|/user/register|{<br>userId: userId<br>pwd: pwd<br>fistName: fistName<br>lastName: lastName<br>birth: birth<br>sex: sex<br>}|{<br>firstName: firstName<br>lastName: lastName<br>sex: sex<br>birth: birth<br>id: id<br>responseMessage: responseMessage<br>userId: userId<br>statusCode: statusCode<br>result: result<br>}|
|사용자 프로필 이미지 변경|PUT|/user/info<br> optional Header :Authorization="Bearer<br>  eyJhbGciOiJIUzI<br> 1NiJ9.eyJzdWIiOiJy<br> b2NraW5d4cCI6<br> MTYzNDY1MzEwN<br> n0.fR3PrXfjAGWD8<br> 5YaXw9dmXSvXcJ9<br> dBtvnb2sHsS9j_U"|{<br>imageUrl: imageUrl<br>}|{<br>statusCode : 200<br>responseMessage: 사용자 이미지 수정 완료<br>userId: userId<br>imageUrl: imageUrl<br>}|
|사용자 정보 조회|POST|/user/info<br> optional Header :<br>Authorization="Bearer<br> eyJhbGciOiJIUzI1<br>NiJ9.eyJzdWIiOiJy<br>b2NraW5<br>d4cCI6MTYzNDY<br>1MzEwNn0.fR3PrXf<br>jAGWD85YaXw9dmXSvXcJ9<br>dBtvnb2sHsS9j_U"|-|{<br>statusCode : 200<br>responseMessage: 사용자 정보 전달<br>userId: userId<br>firstName: firstName<br>lastName: lastName<br>imageUrl: imageUrl<br>}|
|전체 사용자 정보 조회|GET|/user/list<br>Header :<br> Authorization="Bearer <br> eyJhbGciOiJIUzI1N<br> iJ9.eyJzdWIiO<br> iJyb2NraW5d4cCI6MTYz<br> NDY1MzEwNn0.fR3P<br> rXfjAGWD85YaXw<br> 9dmXSvXcJ9dBtvnb2<br> sHsS9j_U"<br> 토큰 헤더 추가하면<br>  로그인한 유저 정보를<br>  뺀 나머지 유저<br>  정보 전달|-|{<br>statusCode : 200<br>responseMessage: 사용자 리스트 전달<br>users:[{<br>userId: userId<br>firstName: firstName<br>lastName: lastName<br>imageUrl: imageUrl<br>}]<br>}|

### 게시글,댓글
|기능　　　　　|Method|URL|Request|Response|
|---|---|---|---|---|
|게시글 작성|POST|/post|{<br>content: content<br>imageUrl: imageUrl<br>}|{<br>statusCode : 200<br>responseMessage: 게시글 작성 성공<br>}|
|게시글 수정|PUT|/post/{postId}<br>Header :<br>Authorization="Bearer<br> eyJhbGciOiJIUz<br>I1NiJ9.eyJzdWIi<br>OiJyb2NraW5d4<br>cCI6MTYzNDY<br>MzEwNn0.fR3PrX<br>fjAGWD85YaXw9<br>dmXSvXcJ9dBt<br>vnb2sHsS9j_U"|{<br>content: content<br>imageUrl: imageUrl<br>}|{<br>post: {<br>postId: postId<br>content: content<br>imageUrl: imageUrl<br>createdAt: createdAt<br>firstName: firstName<br>lastName: lastName<br>likeCount: likeCount<br>commentCount: commentCount<br>comments: comments<br>liked: liked<br>},<br>responseMessage: <br>responseMessage<br>statusCode: <br>statusCode<br>}|
|게시글 삭제|DELETE|/post/{postId}<br>Header : Authorization="Bearer<br> eyJhbGciOiJIUzI<br>1NiJ9.eyJzdWIiOiJyb<br>2NraW5d4cCI6MTYzNDY1<br>MzEwNn0.fR3PrX<br>fjAGWD85YaXw9dmX<br>SvXcJ9dBtvnb2<br>sHsS9j_U"|-|{<br>statusCode : 200<br>responseMessage: 게시글 삭제 성공<br>}|
|게시글 조회|GET|/post?page=page<br>optional <br>Header : Authorization="Bearer<br> eyJhbGciOiJIUzI1Ni<br>J9.eyJzdWIiOiJ<br>yb2NraW5d4cCI6MTYzND<br>Y1MzEwNn0.fR3PrXf<br>jAGWD85YaXw9dmXS<br>vXcJ9dBtvnb2s<br>HsS9j_U"<br>토큰 헤더 추가하면<br> 로그인한 사용자의<br> 게시글별 좋아요<br> 상태 확인 가능<br>추가하지 않으면 <br>모든 게시글 좋아요<br> 상태 false 및 <br>username = "guest"|-|{<br>statusCode : 200<br>responseMessage: 게시글 조회 성공<br>page: page<br>totalPage: totalPage<br>username: username<br>userImageUrl: userImageUrl<br>posts:[{<br>postId: postId<br>content: content<br>imageUrl: imageUrl<br>createdAt: createdAt<br>firstName: firstName<br>lastName: lastName<br>likeCount: likeCount<br>commentCount: commentCount<br>isLiked: isLiked<br>comments: [{<br>commentId:commentId<br>content:content<br>userImageUrl: userImageUrl<br>createdAt: createdAt<br>userId: userId<br>firstName: firstName<br>lastName: lastName}]<br>}]<br>}|
|댓글삭제|DELETE|/comment/{commentId}<br>Header :<br> Authorization="Bearer <br>eyJhbGciOiJ<br>IUzI1NiJ9.eyJzdW<br>IiOiJyb2Nr<br>aW5d4cCI6MTY<br>NDY1MzEwNn0.f<br>R3PrXfjAGWD85Ya<br>Xw9dmXSvXcJ9d<br>Btvnb2sHsS9j_U"|-|{<br>statusCode : 200<br>responseMessage: 댓글 삭제 성공<br>postId: postId<br>}|
|댓글수정|PUT|/comment/{commentId}<br>Header :<br> Authorization="Bearer<br> eyJhbGciOiJIUzI1Ni<br>J9.eyJzdWIiOiJyb2NraW<br>5d4cCI6MTYzNDY1Mz<br>EwNn0.fR3PrXfjAGWD<br>85YaXw9dmXSvXc<br>J9dBtvnb2sHsS9j_U"|{<br>content: content<br>}|{<br>comment: {<br>commentId: commentId<br>content: content<br>createdAt: createdAt<br>userId: userId<br>userImageUrl: userImageUrl<br>firstName: firstName<br>lastName: lastName<br>},<br>postId: postId<br>responseMessage: 댓글 수정 성공<br>statusCode: 200<br>}|
|댓글작성|POST|/comment<br>Header : <br>Authorization="Bearer<br> eyJhbGciOiJIUzI1N<br>iJ9.eyJzdWIiOiJyb<br>2NraW5d4cCI6MTYzND<br>Y1MzEwNn0.fR3Pr<br>XfjAGWD85YaXw9dm<br>XSvXcJ9dBtvn<br>b2sHsS9j_U"|{<br>content: content<br>postId: postId<br>}|{<br>statusCode : 200<br>responseMessage: 댓글 생성 성공<br>comment: {<br>commentId:commentId<br>content:content<br>userImageUrl: userImageUrl<br>createdAt: createdAt<br>userId: userId<br>firstName: firstName<br>lastName: lastName}<br>}|
|좋아요 변경|POST|/post/{postId}/like<br>Header : <br>Authorization="Bearer<br> eyJhbGciOiJIUzI<br>1NiJ9.eyJzdWIiOiJyb<br>2NraW5d4cCI6MTYzNDY<br>1MzEwNn0.fR3PrXfj<br>AGWD85YaXw9dmXSvX<br>cJ9dBtvnb2sHsS9j_U"|-|{<br>isLiked: isLiked<br>statusCode : 200<br>responseMessage: 좋아요 변경 성공<br>}|



 </div></details>

<details markdown = "1">
<summary>
Diagrams
</summary>
 <div>
<img src= "https://media.vlpt.us/images/junseokoo/post/982e77ed-0fde-4d5b-8dcd-3ddfed639e69/image.png">
 </div></details>


<details markdown = "1">
<summary>
문제점 / 해결과정
</summary>

## CORS
+ Cross-origin resource sharing(CORS)는 최초에 리소스를 제공한 출처(origin)와 다른 출처의 리소스를 요청하는 경우(cross-origin 요청), 특정 HTTP header를 사용하여 웹 애플리케이션의 cross-origin 요청을 브라우저가 제한적으로 허용하는 정책입니다.
+ 프론트 측에서 CORS를 전부 허용해달라고 요청을 했었습니다. 그래서 Access-Control-Allow-Orign 에 *을 줬는데 CORS에러가 떠서 
Webconfig에 있는 addCorsMappings 를 WebSecurityConfig에 옮겨서도 해봤지만 똑같이 CORS에러가 떠서 Access-Control-Allow-Origin에대해 구글링해서 해답을 찾았습니다. 그이유는 아래와 같습니다.
+ Access-Control-Allow-Origin: *와 Access-Control-Allow-Credentials: true는 함께 사용할 수 없다.
  CORS는 응답이 Access-Control-Allow-Credentials: true 을 가질 경우, Access-Controll-Allow-Origin의 값으로 *를 사용하지 못하게 막고 있습니다.
+ Access-Control-Allow-Credentials: true를 사용하는 경우는 사용자 인증이 필요한 리소스 접근이 필요한 경우인데, 만약 Access-Control-Allow-Origin: *를 허용한다면, CSRF 공격에 매우 취약해져 악의적인 사용자가 인증이 필요한 리소스를 마음대로 접근할 수 있습니다. 그렇기 때문에 CORS 정책에서 아예 동작하지 않도록 막아버린 것입니다.
+ Access-Contorl-Allow-Credentials: true인 경우에는 반드시 Access-Control-Allow-Origin의 값이 하나의 origin 값으로 명시되어 있어야 정상적으로 동작합니다.

</details>

[YOUTUBE 영상](http://youtube.com) 