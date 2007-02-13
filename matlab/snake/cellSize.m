function d=cellSize(Image); %Calculates the medium cell size

imagesc(Image), colormap(gray);
hold on;
disp('Measure the diameter on three different cells.')

for k=1:3
    [x,y]=ginput(2); %The user clicks twice 
    D(k)=sqrt((x(1)-x(2))^2+(y(1)-y(2))^2);
    plot(x,y,'g-')
    drawnow
end
d=mean(D);
