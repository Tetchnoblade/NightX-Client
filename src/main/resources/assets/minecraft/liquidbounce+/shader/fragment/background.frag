uniform vec2      iResolution;
uniform float     iTime;

float rand(vec2 n) {
    return fract(cos(dot(n, vec2(2.9898, 20.1414))) * 5.5453);
}

float noise(vec2 n) {
    const vec2 d = vec2(0.0, 1.0);
    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

float fbm(vec2 n){
    float total=0.,amplitude=1.5;
    for(int i=0;i<18;i++){
        total+=noise(n)*amplitude;
        n+=n;
        amplitude*=.45;
    }
    return total;
}


void main(void){
    const vec3 c1=vec3(0.802, 0.1059, 0.01059);
    const vec3 c2=vec3(167./255.,96./255.,110./255.);
    const vec3 c3=vec3(0.4902, 0.2333, 0.2902);
    const vec3 c4=vec3(0.4118, 0.1451, 0.2706);
    const vec3 c5=vec3(0.4176, 0.2549, 0.1);
    const vec3 c6=vec3(0.8, 0.3569, 0.3569);

    vec2 p=gl_FragCoord.xy*5./iResolution.xx;
    float q=fbm(p-iTime*.05);
    vec2 r=vec2(fbm(p+q+iTime*0.1-p.x-p.y),fbm(p+q-iTime*0.1));
    vec3 c=mix(c1,c2,fbm(p+r))+mix(c3,c4,r.x)-mix(c5,c6,r.y);
    float grad=gl_FragCoord.y/iResolution.y;
    gl_FragColor=vec4(c*cos(1.0*gl_FragCoord.y/iResolution.y),1.5);
    gl_FragColor.xyz*=1.15-grad;
}