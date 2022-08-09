function renderInteractiveMap(selector, scale, activeRooms, inuseRooms, partyInUse = []) {
    let canvas = document.querySelector(selector);
    let ctx = canvas.getContext('2d');
    let b = [];
    canvas.setAttribute("width", 800 * scale);
    canvas.setAttribute("height", 754 * scale);

    class Room {
        constructor(id, path, active, inuse, partly) {
            this.id = id
            this.path = path;
            this.inuse = inuse;
            this.active = active;
            this.partly = partly;
        }

        drawRoom() {
            ctx.fillStyle = this.active ? 'green' : (this.partly ? 'yellow' : (this.inuse ? '#ab2227' : 'gray'));
            ctx.fill(this.path);
            ctx.lineWidth = 2;
            ctx.strokeStyle="#000000";
            ctx.stroke(this.path);
        }
        
        setActive(active) {
            this.active = active
        }
    }

    canvas.addEventListener('mousemove', function(event) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        let currentpath;
        let tooltipText = null;
        for (let i = 0; i < b.length; i++) {
            currentpath = b[i].path;
            if (b[i].inuse) {
                if (ctx.isPointInPath(currentpath, event.offsetX, event.offsetY)) {
                    tooltipText = "The facility is in use!";
                }
                ctx.fillStyle = "#ab2227";

            } else if(b[i].partly) {
                ctx.fillStyle = "yellow";
                if (ctx.isPointInPath(currentpath, event.offsetX, event.offsetY)) {
                    tooltipText = "The facility is in use for part of you reservation!";
                }
            } else {
                if (!b[i].active) {
                    if (ctx.isPointInPath(currentpath, event.offsetX, event.offsetY)) {
                        ctx.fillStyle = "#D3D3D3";
                    } else {
                        ctx.fillStyle = 'gray';
                    }
                } else {
                    if (ctx.isPointInPath(currentpath, event.offsetX, event.offsetY)) {
                        ctx.fillStyle = "#7fc779";
                    } else {
                        ctx.fillStyle = 'green';

                    }
                }
            }

            ctx.lineWidth = 2;
            ctx.fill(currentpath);
            ctx.stroke(currentpath);
        }
        if (tooltipText) {
            ctx.font = '15px arial';
            ctx.lineWidth = 0.5;
            ctx.fillStyle = 'black';
            ctx.strokeText(tooltipText, event.offsetX, event.offsetY)
        }
    });

    canvas.addEventListener('click', function(event) {
        let selectedRoom = b.find(room => ctx.isPointInPath(room.path, event.offsetX, event.offsetY));
        if (selectedRoom) {
            let tooltipText = null;
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.lineWidth = 2;
            b.forEach((room) => {
                if (room.inuse) {
                    ctx.fillStyle = "#ab2227";
                    if (ctx.isPointInPath(room.path, event.offsetX, event.offsetY)) {
                        tooltipText = "The facility is in use!";
                    }
                } else if(room.partly) {
                    ctx.fillStyle = "yellow";
                    if (ctx.isPointInPath(room.path, event.offsetX, event.offsetY)) {
                        tooltipText = "The facility is in use for part of you reservation!";
                    }
                } else {
                    if (room.id === selectedRoom.id && !room.active) {
                        room.setActive(true);
                        ctx.fillStyle = 'green';
                    } else {
                        room.setActive(false);
                        ctx.fillStyle = 'gray';
                    }
                }
                ctx.fill(room.path);
                ctx.stroke(room.path);
            });
            if (tooltipText) {
                ctx.font = '15px arial';
                ctx.lineWidth = 0.5;
                ctx.fillStyle = 'black';
                ctx.strokeText(tooltipText, event.offsetX, event.offsetY)
            }
        }
    });

    const path = new Path2D();
    path.moveTo(90 * scale, 290 * scale);
    path.lineTo(90 * scale, 374 * scale);
    path.lineTo(177 * scale, 374 * scale);
    path.lineTo(177 * scale, 290 * scale);
    path.lineTo(90 * scale, 290 * scale);
    b.push(new Room(1, path, activeRooms.includes(1),
        inuseRooms.includes(1) && !activeRooms.includes(1),
        partyInUse.includes(1) && !activeRooms.includes(1)));

    const path2 = new Path2D();
    path2.moveTo(90 * scale, 374 * scale);
    path2.lineTo(90 * scale, 584 * scale);
    path2.lineTo(177 * scale,584 * scale);
    path2.lineTo(177 * scale, 374 * scale);
    path2.lineTo(90 * scale, 374 * scale);
    b.push(new Room(2, path2, activeRooms.includes(2), inuseRooms.includes(2) && !activeRooms.includes(2),
        partyInUse.includes(2) && !activeRooms.includes(2)));

    const path3 = new Path2D();
    path3.moveTo(90 * scale, 584 * scale);
    path3.lineTo(90 * scale, 668 * scale);
    path3.quadraticCurveTo(177 * scale, 668 * scale, 177 * scale, 584 * scale)
    path3.lineTo(90 * scale, 584 * scale)
    b.push(new Room(3, path3, activeRooms.includes(3), inuseRooms.includes(3) && !activeRooms.includes(3),
        partyInUse.includes(3) && !activeRooms.includes(3)));

    const path4 = new Path2D();
    path4.moveTo(177 * scale, 498 * scale);
    path4.lineTo(177 * scale, 584 * scale);
    path4.lineTo(396 * scale, 584 * scale);
    path4.lineTo(396 * scale, 498 * scale);
    path4.lineTo(177 * scale, 498 * scale);
    b.push(new Room(4, path4, activeRooms.includes(4), inuseRooms.includes(4) && !activeRooms.includes(4),
        partyInUse.includes(4) && !activeRooms.includes(4)));

    // const path5 = new Path2D();
    // path5.moveTo(396 * scale, 545 * scale);
    // path5.lineTo(445 * scale, 545 * scale);
    // path5.lineTo(470 * scale, 570 * scale);
    // path5.lineTo(470 * scale, 698 * scale);
    // path5.quadraticCurveTo(380 * scale, 698 * scale, 380 * scale, 584 * scale);
    // path5.lineTo(396 * scale, 584 * scale);
    // path5.lineTo(396 * scale, 545 * scale);
    // b.push(new Room(5, path5, activeRooms.includes(5), inuseRooms.includes(5) && !activeRooms.includes(5),
    //     partyInUse.includes(5) && !activeRooms.includes(5)));

    // const path6 = new Path2D();
    // path6.moveTo(396 * scale, 545 * scale);
    // path6.lineTo(445 * scale, 545 * scale);
    // path6.lineTo(470 * scale, 570 * scale);
    // path6.lineTo(470 * scale, 603 * scale);
    // path6.lineTo(591 * scale, 603 * scale);
    // path6.lineTo(591 * scale, 570 * scale);
    // path6.lineTo(616 * scale, 545 * scale);
    // path6.lineTo(685 * scale, 545 * scale);
    // path6.lineTo(685 * scale, 303 * scale);
    // path6.lineTo(471 * scale, 303 * scale);
    // path6.lineTo(471 * scale, 390 * scale);
    // path6.lineTo(396 * scale, 390 * scale);
    // path6.lineTo(396 * scale, 545 * scale);
    // b.push(new Room(6, path6, activeRooms.includes(6), inuseRooms.includes(6) && !activeRooms.includes(6),
    //     partyInUse.includes(6) && !activeRooms.includes(6)));


    const path7 = new Path2D();
    path7.moveTo(396 * scale, 390 * scale);
    path7.lineTo(396 * scale, 448 * scale);
    path7.lineTo(356 * scale, 448 * scale);
    path7.lineTo(356 * scale, 390 * scale);
    path7.lineTo(396 * scale, 390 * scale);
    b.push(new Room(7, path7, activeRooms.includes(7), inuseRooms.includes(7) && !activeRooms.includes(7),
        partyInUse.includes(7) && !activeRooms.includes(7)));

    const path8 = new Path2D();
    path8.moveTo(356 * scale, 390 * scale);
    path8.lineTo(356 * scale, 303 * scale);
    path8.lineTo(471 * scale, 303 * scale);
    path8.lineTo(471 * scale, 390 * scale);
    path8.lineTo(356 * scale, 390 * scale);
    b.push(new Room(8, path8, activeRooms.includes(8), inuseRooms.includes(8) && !activeRooms.includes(8),
        partyInUse.includes(8) && !activeRooms.includes(8)));

    const path9 = new Path2D();
    path9.moveTo(471 * scale, 303 * scale);
    path9.lineTo(356 * scale, 303 * scale);
    path9.lineTo(356 * scale, 231 * scale);
    path9.lineTo(471 * scale, 231 * scale);
    path9.lineTo(471 * scale, 303 * scale);
    b.push(new Room(9, path9, activeRooms.includes(9), inuseRooms.includes(9) && !activeRooms.includes(9),
        partyInUse.includes(9) && !activeRooms.includes(9)));

    const path10 = new Path2D();
    path10.moveTo(177 * scale, 498 * scale);
    path10.lineTo(396 * scale, 498 * scale)
    path10.lineTo(396 * scale, 448 * scale);
    path10.lineTo(356 * scale, 448 * scale);
    path10.lineTo(356 * scale, 231 * scale);
    path10.lineTo(471 * scale, 231 * scale);
    path10.lineTo(471 * scale, 303 * scale);
    path10.lineTo(685 * scale, 303 * scale);
    path10.lineTo(685 * scale, 95 * scale);
    path10.lineTo(177 * scale, 95 * scale);
    path10.lineTo(177 * scale, 498 * scale)
    b.push(new Room(10, path10, activeRooms.includes(10), inuseRooms.includes(10) && !activeRooms.includes(10),
        partyInUse.includes(10) && !activeRooms.includes(10)));

    // const path11 = new Path2D();
    // path11.moveTo(177 * scale, 95 * scale);
    // path11.lineTo(177 * scale, 15 * scale);
    // path11.lineTo(387 * scale, 15 * scale);
    // path11.lineTo(387 * scale, 95 * scale);
    // path11.lineTo(177 * scale, 95 * scale);
    // b.push(new Room(11, path11, activeRooms.includes(11), inuseRooms.includes(11) && !activeRooms.includes(11),
    //     partyInUse.includes(11) && !activeRooms.includes(11)));

    const path12 = new Path2D();
    path12.moveTo(387 * scale, 15 * scale);
    path12.lineTo(387 * scale, 95 * scale)
    path12.lineTo(467 * scale, 95 * scale);
    path12.lineTo(467 * scale, 15 * scale);
    path12.lineTo(387 * scale, 15 * scale);
    b.push(new Room(12, path12, activeRooms.includes(12), inuseRooms.includes(12) && !activeRooms.includes(12),
        partyInUse.includes(12) && !activeRooms.includes(12)));

    function loadFunctions() {
        b.forEach(function(Room) {
            Room.drawRoom();
        })
    }

    function getActiveRoom() {
        return b.find(room => room.active) || null;
    }

    loadFunctions();

    return getActiveRoom;
}