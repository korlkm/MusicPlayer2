# MusicPlayer2
간단하게 구성만 함
// player를 전역 변수로 선언하여 Android Life Cycle에 맞춰서 player를 다룸
// 플레이할 샘플 노래의 url을 전역변수로 선언
//
//onCreate 함수에 initializePlayer 함수를 추가하고, onCreated 함수 하단에 private으로 initializePlayer 함수를 정의해준다.
//
//initializePlayer에서는 player가 null인지 체크, null인 경우 새로운 SimpleExoPlayer 객체를 만들어주고,  MainActivity xml파일에 만든 main_pcv라는 PlayerControlView에 player를 넣어준다.
//
//DefaultHttpDataSourceFactory를 사용해서 DataSourceFactory 객체 만듬 BaseDataSource를 상속받은 DataSource를 생성한다.
//
// ProgressiveMediaSource를 이용해서 mediaSource를 만듬
