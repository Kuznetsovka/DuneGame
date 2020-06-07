package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dune.game.core.units.AbstractBuild;
import com.dune.game.core.units.Base;
import com.dune.game.core.units.Owner;

import java.util.ArrayList;
import java.util.List;

public class BuildController extends ObjectPool<Base> {
        private GameController gc;
        private List<AbstractBuild> builds;

    public List<AbstractBuild> getBuilds() {
        return builds;
    }

    public BuildController(GameController gc) {
        this.gc = gc;
        this.builds = new ArrayList<> ();
        createBase(Owner.PLAYER, MathUtils.random(80, 1200), MathUtils.random(80, 640));
        createBase(Owner.AI, MathUtils.random(80, 1200), MathUtils.random(80, 640));
        }

        public void createBase(Owner owner, float x, float y) {
            Base t = activateObject();
            builds.add(t);
            t.setup(owner, x, y);
        }

    @Override
    protected Base newObject() {
        return new Base(gc);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void update(float dt){
        builds.clear ();
        for (int i = 0; i < activeList.size (); i++) {
            activeList.get (i).update (dt);
            builds.add (activeList.get (i));
        }
    }
}
