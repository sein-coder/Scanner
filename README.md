# Scanner

### 바코드 스캐너 앱 ( QR도 인식은 가능 )

#### Scanner(v1.0)
- zxing lib 활용 ( v3.4.0 )
- com.journeyapps.barcodescanner lib 활용 ( v3.5.0 )
- 내장 DB sqlite 활용 ( 1.0 )

#### Scanner(v1.1)
- 결재시 확인 다이얼로그 추가
- 결재 금액 계산 버그 수정
- 최하단에 스크롤 맞추게 변경

#### Scanner(v1.2)
- 삭제 기능 추가(다이얼로그)
- 커스텀 ListView 활용
- 이미지 파일 추가
- APK 파일 업데이트

- 삭제 기능 변경
  - 이미지 버튼이 아닌 행 전체 클릭 이벤트
- 오타 수정 (결재 -> 결제)

#### Scanner(v2.0)
- 이미지 분별 기능 추가
- Google Teachable Machine 활용
- 모델 개선
- Layout View 개선

#### Scanner(v2.0)
- 장바구니 새로고침 버그 수정

### 미해결 버그
- 너무 빠르게 연속으로 바코드 스캔시 상품인식 누락문제
