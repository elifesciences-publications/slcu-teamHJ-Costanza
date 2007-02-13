%Segmenting the bud from the background

clear all;

maxiterFirst=35; %max number of iterations in first image
maxiter=35; %%max number of iterations
my=0.1; %weight in gvf calculation
GVFiter=40; %number gvf calculation iterations
alpha=1;    %snake elasticity parameter
beta=1;     %snake rigidity parameter
gamma=1;    %snake viscosity parameter
kappa=0.2;   %snake external force weight
iter=5;     %The number of iterations between every plot
dmax=3;     %The maximum distance between two snake points
dmin=1;     %The miniimum distance between two snake points

%Making the snake files usable
s = cd; path(s, path);
%cd ..; s=cd; s = [s, '/snake']; path(s, path); cd project; 
s = [s, '/snake']; path(s, path); 

thisDir = cd;
thisDir = [thisDir, '/'];

imageDirName=input('What is the path to the image directory? ','s');
%s=cd; s = [s, '/', imageDirName]; path(s, path); 
bgDirName=input('What is the path to the background directory? ','s');

numOfImages=input('How many images do you have? ');
numOfImages=numOfImages-1; %Since I start with zero
name=input('What is the name of the last one? ','s');
D=[name,'.bg'];

for kk=0:1:numOfImages
    i=numOfImages-kk;
    %cd ../..;
    %cd images/flower2/membrane;
    cd(imageDirName);
    R=imread(name);
    cd(thisDir)  ; 
    %cd ../../..;
    %cd matlab/project;
    
    if isgray(R)
    else
        R=rgb2gray(R);  
    end
    
    R=double(R);
    
    %If you want to make the images smaller
    %[m,n]=size(R);
    %factor=round(m/300);
    %if factor>1
    %    R=R(1:factor:m,1:factor:n);
    %end
    
 
    if i==numOfImages;
       %d=cellSize(R);
       %factor=round(d/12.5);
        disp('Click around the bud.');
        [xin,yin]=getsnake(R);
        %load start
        %xin=xstart;
        %yin=ystart;
        xstart=xin;
        ystart=yin;
        maxiter=maxiterFirst;
    else
        xin=x;
        yin=y;
        maxiter=maxiter;
    end
    
    [x,y]=snakeinterp(xin,yin,dmax,dmin);
    snakedisp(x,y,'r')
    pause(1);
    
    %Make the brighter pixels darker
    Rmod=R;
    %m=max(max(R));
    %idx=find(R>m/3);
    %Rmod(idx)=m/3;

    %Run gaussian convolution
    factor=1;
    F=conv2(Rmod,fspecial('gaussian',27*factor,81*factor),'same');
    
    %If the gradient is used
    [gx,gy]=gradient(F);
    G=sqrt(gx.^2+gy.^2);
    [u,v]=GVF(G,my,GVFiter);

    %normalizing of the u,v and enlarge small values
    mag = sqrt(u.*u+v.*v);
    W=1/pi*atan(mag-4)+1.2;
    px = W.*(u./(mag+1e-10)); py = W.*(v./(mag+1e-10)); 

   
    for k=1:maxiter,
       [x,y] = snakedeform(x,y,alpha,beta,gamma,kappa,px,py,iter);
       [x,y] = snakeinterp(x,y,dmax,dmin); 
       axis('square', 'on');
       imagesc(R),colormap(gray),hold on,snakedisp(x,y,'r') 
       title(['Deformation in progress,  iter = ' num2str(k*iter)])
       pause(0.5);
   end
         
     figure(i+1),clf,imagesc(R);colormap(gray);
     snakedisp(x,y,'r'), drawnow
     title(['Final result,  iter = ' num2str(k*iter)]);
     
     [J,BW]=roifill(R,round(x),round(y));
     [ii,jj]=find(BW==0);
     ii=ii-1;
     jj=jj-1;
     bg=[length(ii),2;ii,jj];
     bg=bg';
     
     %%%%%%%%
     
    %This is needed for printing the background pixels
    
     %cd ../../images/flower2/membrane/bg/;
     cd(bgDirName);
     fid=fopen(D,'w');
     fprintf(fid, '%d %d\n',bg);
     fclose(fid);
     cd(thisDir);
     %cd ../../../../matlab/project/;
     
     %%%%%%%%%%
     
 
   
    nameLength=length(name);
    %Assuming the filename ends with .tif
   if i>30
       name(nameLength-4)=name(nameLength-4)-1;
       D(3)=D(3)-1;
   elseif i==30
       name(nameLength-5)=strcat('2');
       name(nameLength-4)=strcat('9'); 
       D(nameLength-5)=strcat('2');
       D(nameLength-4)=strcat('9');
   elseif i>20
       name(nameLength-4)=name(nameLength-4)-1;
       D(3)=D(3)-1;
   elseif i==20
       name(nameLength-5)=strcat('1');
       name(nameLength-4)=strcat('9'); 
       D(nameLength-5)=strcat('1');
       D(nameLength-4)=strcat('9');
   elseif i>10
       name(nameLength-4)=name(nameLength-4)-1;
       D(nameLength-4)=D(nameLength-4)-1;
   elseif i==10
       name(nameLength-5)=strcat('0');
       name(nameLength-4)=strcat('9');
       D(nameLength-5)=strcat('0');
       D(nameLength-4)=strcat('9');
   else
       name(nameLength-4)=name(nameLength-4)-1;
       D(nameLength-4)=D(nameLength-4)-1;
   end
                  
     clear ii;
     clear jj;
     
 end
   
